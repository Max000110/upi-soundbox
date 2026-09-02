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
            val list = mutableListOf<PaymentEvent>()
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

                // Migration / Backward Compatibility: derive durableIdentity and announcementState if missing
                val durableId = if (obj.has("durableIdentity") && !obj.isNull("durableIdentity") && obj.getString("durableIdentity").isNotBlank()) {
                    obj.getString("durableIdentity")
                } else {
                    PaymentEvent.generateDurableIdentity(
                        provider = provider,
                        amountMinor = amountMinor,
                        payerName = payerName,
                        transactionReference = transactionReference,
                        sourceNotificationKey = sourceNotificationKey
                    )
                }

                val annState = if (obj.has("announcementState") && !obj.isNull("announcementState")) {
                    try { AnnouncementState.valueOf(obj.getString("announcementState")) } catch (e: Exception) { AnnouncementState.ANNOUNCED }
                } else {
                    AnnouncementState.ANNOUNCED
                }

                val annAt = obj.optLong("announcedAt", eventTime)

                list.add(
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
            list
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
     * Checks whether an event matching the given durable identity, transaction reference,
     * or source notification key has already been marked ANNOUNCED in persistent storage.
     */
    fun isAlreadyAnnounced(event: PaymentEvent): Boolean {
        synchronized(lock) {
            val list = _history.value
            val cleanRef = event.transactionReference?.trim()?.takeIf { it.isNotEmpty() }
            val cleanKey = event.sourceNotificationKey?.trim()?.takeIf { it.isNotEmpty() }

            for (stored in list) {
                if (stored.announcementState != AnnouncementState.ANNOUNCED) continue

                // 1. Exact Durable Identity Match
                if (stored.durableIdentity == event.durableIdentity) {
                    Log.d("UpiSoundbox", "isAlreadyAnnounced: Match by durableIdentity: ${event.durableIdentity}")
                    return true
                }

                // 2. Exact Transaction Reference Match (across providers / SMS / App)
                if (cleanRef != null && !stored.transactionReference.isNullOrBlank()) {
                    if (stored.transactionReference.trim().equals(cleanRef, ignoreCase = true) &&
                        stored.amountMinor == event.amountMinor &&
                        stored.provider == event.provider
                    ) {
                        Log.d("UpiSoundbox", "isAlreadyAnnounced: Match by transactionReference: $cleanRef")
                        return true
                    }
                }

                // 3. Exact Source Notification Key Match
                if (cleanKey != null && !stored.sourceNotificationKey.isNullOrBlank()) {
                    if (stored.sourceNotificationKey.trim() == cleanKey &&
                        stored.amountMinor == event.amountMinor &&
                        stored.provider == event.provider
                    ) {
                        Log.d("UpiSoundbox", "isAlreadyAnnounced: Match by sourceNotificationKey: $cleanKey")
                        return true
                    }
                }
            }
            return false
        }
    }

    /**
     * Atomically records an event into history marked as ANNOUNCED and saves to persistent storage.
     */
    fun recordAndMarkAnnounced(event: PaymentEvent) {
        synchronized(lock) {
            val current = _history.value.toMutableList()
            // Check if already in list to avoid duplicates in list
            val existingIndex = current.indexOfFirst {
                it.durableIdentity == event.durableIdentity ||
                (!event.transactionReference.isNullOrBlank() && it.transactionReference.equals(event.transactionReference, ignoreCase = true))
            }

            val announcedEvent = event.copy(
                announcementState = AnnouncementState.ANNOUNCED,
                announcedAt = System.currentTimeMillis()
            )

            if (existingIndex >= 0) {
                current[existingIndex] = announcedEvent
            } else {
                current.add(0, announcedEvent)
                if (current.size > 200) {
                    current.removeAt(current.lastIndex)
                }
            }

            _history.value = current
            saveToDiskAsync(current)
            Log.i("UpiSoundbox", "recordAndMarkAnnounced: Persisted event ${announcedEvent.amountMajorFormatted} (durableId=${announcedEvent.durableIdentity})")
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
