package com.upisoundbox.storage

import android.content.Context
import android.util.Log
import com.upisoundbox.core.model.AnnouncementState
import com.upisoundbox.core.model.Currency
import com.upisoundbox.core.model.Direction
import com.upisoundbox.core.model.Provider
import com.upisoundbox.domain.model.PaymentEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar
import kotlin.math.abs

class HistoryRepository(
    private val context: Context,
    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    private val prefs = context.getSharedPreferences("upi_payment_history_store", Context.MODE_PRIVATE)
    private val keyHistoryJson = "payment_history_json_v1"

    private val _history = MutableStateFlow<List<PaymentEvent>>(loadPersistedHistory())
    val history: StateFlow<List<PaymentEvent>> = _history.asStateFlow()

    private val lock = Any()

    private fun loadPersistedHistory(): List<PaymentEvent> {
        val jsonStr = prefs.getString(keyHistoryJson, null)
        if (jsonStr.isNullOrBlank()) {
            return emptyList()
        }

        return try {
            val array = JSONArray(jsonStr)
            val rawList = mutableListOf<PaymentEvent>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val provider = try { Provider.valueOf(obj.getString("provider")) } catch (e: Exception) { Provider.GENERIC }
                val direction = try { Direction.valueOf(obj.getString("direction")) } catch (e: Exception) { Direction.CREDIT }
                val amountMinor = obj.optLong("amountMinor", 100L)
                val payerName = if (obj.has("payerName") && !obj.isNull("payerName")) obj.getString("payerName") else null
                val transactionReference = if (obj.has("transactionReference") && !obj.isNull("transactionReference")) obj.getString("transactionReference") else null
                val eventTime = obj.optLong("eventTime", System.currentTimeMillis())
                val sourceNotificationKey = obj.optString("sourceNotificationKey", "")
                val confidence = obj.optDouble("confidence", 0.95).toFloat()
                val rawSnippet = obj.optString("rawSnippet", "")

                val durableId = PaymentEvent.generateDurableIdentity(
                    provider = provider,
                    amountMinor = amountMinor,
                    payerName = payerName,
                    transactionReference = transactionReference,
                    sourceNotificationKey = sourceNotificationKey
                )

                val annState = if (obj.has("announcementState") && !obj.isNull("announcementState")) {
                    try { AnnouncementState.valueOf(obj.getString("announcementState")) } catch (e: Exception) { AnnouncementState.ANNOUNCED }
                } else {
                    AnnouncementState.ANNOUNCED
                }

                val annAt = obj.optLong("announcedAt", eventTime)

                rawList.add(
                    PaymentEvent(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        sourcePackage = obj.optString("sourcePackage", "com.google.android.apps.nbu.paisa.user"),
                        provider = provider,
                        direction = direction,
                        amountMinor = amountMinor,
                        currency = Currency.INR,
                        payerName = payerName,
                        transactionReference = transactionReference,
                        eventTime = eventTime,
                        sourceNotificationKey = sourceNotificationKey,
                        confidence = confidence,
                        rawSnippet = rawSnippet,
                        durableIdentity = durableId,
                        announcementState = annState,
                        announcedAt = annAt
                    )
                )
            }

            // Deduplicate historical list: merge identical (payer + amount within 2h) Bank/UPI pairs
            val cleanList = mutableListOf<PaymentEvent>()
            for (item in rawList) {
                val isDuplicateOfExisting = cleanList.any { existing ->
                    if (existing.amountMinor != item.amountMinor) return@any false
                    if (!item.transactionReference.isNullOrBlank() && !existing.transactionReference.isNullOrBlank()) {
                        return@any existing.transactionReference.equals(item.transactionReference, ignoreCase = true)
                    }
                    val payerMatch = arePayersMatching(existing.payerName, item.payerName)
                    val timeDiff = abs(existing.eventTime - item.eventTime)
                    payerMatch && (timeDiff < 2 * 3600 * 1000L)
                }
                if (!isDuplicateOfExisting) {
                    cleanList.add(item)
                }
            }

            if (cleanList.size != rawList.size) {
                saveToDiskAsync(cleanList)
                Log.i("UpiSoundbox", "Cleaned up historical duplicates: ${rawList.size} -> ${cleanList.size} unique transactions")
            }

            cleanList
        } catch (e: Exception) {
            Log.e("UpiSoundbox", "Error loading persisted payment history", e)
            emptyList()
        }
    }

    private fun saveToDiskAsync(events: List<PaymentEvent>) {
        ioScope.launch {
            try {
                val array = JSONArray()
                for (event in events) {
                    val obj = JSONObject().apply {
                        put("id", event.id)
                        put("sourcePackage", event.sourcePackage)
                        put("provider", event.provider.name)
                        put("direction", event.direction.name)
                        put("amountMinor", event.amountMinor)
                        put("payerName", event.payerName)
                        put("transactionReference", event.transactionReference)
                        put("eventTime", event.eventTime)
                        put("sourceNotificationKey", event.sourceNotificationKey)
                        put("confidence", event.confidence.toDouble())
                        put("rawSnippet", event.rawSnippet)
                        put("durableIdentity", event.durableIdentity)
                        put("announcementState", event.announcementState.name)
                        put("announcedAt", event.announcedAt)
                    }
                    array.put(obj)
                }
                prefs.edit().putString(keyHistoryJson, array.toString()).apply()
            } catch (e: Exception) {
                Log.e("UpiSoundbox", "Error saving payment history to disk", e)
            }
        }
    }

    /**
     * Checks whether an event matching the transaction reference, notification key,
     * or cross-provider payer+amount pair has already been announced.
     */
    fun isAlreadyAnnounced(event: PaymentEvent): Boolean {
        synchronized(lock) {
            val list = _history.value
            val cleanRef = event.transactionReference?.trim()?.takeIf { it.isNotEmpty() }
            val cleanKey = event.sourceNotificationKey?.trim()?.takeIf { it.isNotEmpty() }

            for (stored in list) {
                if (stored.announcementState != AnnouncementState.ANNOUNCED) continue

                // 1. Exact Transaction Reference Match (Cross-Provider & Bank SMS)
                if (cleanRef != null && !stored.transactionReference.isNullOrBlank()) {
                    if (stored.transactionReference.trim().equals(cleanRef, ignoreCase = true) &&
                        stored.amountMinor == event.amountMinor
                    ) {
                        Log.w("UpiSoundbox", "isAlreadyAnnounced: Suppressed by global transaction reference match: $cleanRef")
                        return true
                    }
                }

                // 2. Exact Notification Key Match
                if (cleanKey != null && !stored.sourceNotificationKey.isNullOrBlank()) {
                    if (stored.sourceNotificationKey.trim() == cleanKey &&
                        stored.amountMinor == event.amountMinor
                    ) {
                        Log.w("UpiSoundbox", "isAlreadyAnnounced: Suppressed by exact notification key match: $cleanKey")
                        return true
                    }
                }

                // 3. Cross-Provider Multi-App Reconciliation Match (Bank Push vs Google Pay/PhonePe/Paytm Push)
                if (stored.amountMinor == event.amountMinor) {
                    val payerMatch = arePayersMatching(stored.payerName, event.payerName)
                    val timeDiff = abs(event.eventTime - stored.eventTime)

                    if (payerMatch && timeDiff < 2 * 3600 * 1000L) {
                        Log.w("UpiSoundbox", "isAlreadyAnnounced: Suppressed cross-provider duplicate payment (${event.amountMajorFormatted} from ${event.payerName ?: "Customer"} within ${timeDiff / 1000}s)")
                        return true
                    }
                }
            }
            return false
        }
    }

    private fun arePayersMatching(p1: String?, p2: String?): Boolean {
        if (p1.isNullOrBlank() && p2.isNullOrBlank()) return true
        if (p1.isNullOrBlank() || p2.isNullOrBlank()) return true

        val s1 = PaymentEvent.cleanPayer(p1)?.lowercase() ?: ""
        val s2 = PaymentEvent.cleanPayer(p2)?.lowercase() ?: ""

        if (s1.isEmpty() || s2.isEmpty()) return true
        return s1 == s2 || s1.contains(s2) || s2.contains(s1)
    }

    /**
     * Atomically records an event into history marked as ANNOUNCED and saves to persistent storage.
     */
    fun recordAndMarkAnnounced(event: PaymentEvent) {
        synchronized(lock) {
            val current = _history.value.toMutableList()
            val existingIndex = current.indexOfFirst {
                if (it.amountMinor != event.amountMinor) return@indexOfFirst false
                if (!event.transactionReference.isNullOrBlank() && !it.transactionReference.isNullOrBlank()) {
                    return@indexOfFirst it.transactionReference.equals(event.transactionReference, ignoreCase = true)
                }
                val payerMatch = arePayersMatching(it.payerName, event.payerName)
                val timeDiff = abs(it.eventTime - event.eventTime)
                payerMatch && (timeDiff < 2 * 3600 * 1000L)
            }

            val announcedEvent = event.copy(
                announcementState = AnnouncementState.ANNOUNCED,
                announcedAt = System.currentTimeMillis()
            )

            if (existingIndex >= 0) {
                // If existing record was generic (Bank SMS / Bank Push) and new one has specific provider, upgrade provider info
                val existing = current[existingIndex]
                val bestProvider = if (existing.provider == Provider.GENERIC && event.provider != Provider.GENERIC) {
                    event.provider
                } else {
                    existing.provider
                }
                current[existingIndex] = existing.copy(
                    provider = bestProvider,
                    transactionReference = existing.transactionReference ?: event.transactionReference,
                    announcementState = AnnouncementState.ANNOUNCED,
                    announcedAt = System.currentTimeMillis()
                )
            } else {
                current.add(0, announcedEvent)
                if (current.size > 200) {
                    current.removeAt(current.lastIndex)
                }
            }

            _history.value = current
            saveToDiskAsync(current)
            Log.i("UpiSoundbox", "recordAndMarkAnnounced: Persisted event ${announcedEvent.amountMajorFormatted} (${announcedEvent.provider.displayName} from ${announcedEvent.payerName})")
        }
    }

    fun addEvent(event: PaymentEvent) {
        recordAndMarkAnnounced(event)
    }

    fun clearHistory() {
        synchronized(lock) {
            _history.value = emptyList()
            ioScope.launch {
                prefs.edit().remove(keyHistoryJson).apply()
            }
        }
    }

    fun getTodayStats(): Pair<Long, Int> {
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val todayEvents = _history.value.filter { it.eventTime >= startOfDay }
        val totalMinor = todayEvents.sumOf { it.amountMinor }
        return Pair(totalMinor, todayEvents.size)
    }
}
