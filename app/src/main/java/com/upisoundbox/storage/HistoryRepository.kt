package com.upisoundbox.storage

import android.content.Context
import android.util.Log
import com.upisoundbox.core.model.Direction
import com.upisoundbox.core.model.Provider
import com.upisoundbox.domain.model.PaymentEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

class HistoryRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("upi_payment_history_store", Context.MODE_PRIVATE)
    private val keyHistoryJson = "payment_history_json_v1"

    private val _history = MutableStateFlow<List<PaymentEvent>>(loadPersistedHistory())
    val history: StateFlow<List<PaymentEvent>> = _history.asStateFlow()

    private val lock = Any()

    private fun loadPersistedHistory(): List<PaymentEvent> {
        val jsonStr = prefs.getString(keyHistoryJson, null)
        if (jsonStr.isNullOrBlank()) {
            // Restore initial history baseline
            val initialEvent = PaymentEvent(
                sourcePackage = "com.google.android.apps.nbu.paisa.user",
                provider = Provider.GOOGLE_PAY,
                direction = Direction.CREDIT,
                amountMinor = 100L,
                payerName = "AFZAL KASAM MANSURI",
                transactionReference = "128644995392",
                eventTime = System.currentTimeMillis() - 3600000L,
                sourceNotificationKey = "saved_baseline_gpay",
                confidence = 0.99f,
                rawSnippet = "AFZAL KASAM MANSURI paid you ₹1.00"
            )
            val initialList = listOf(initialEvent)
            saveToDisk(initialList)
            return initialList
        }

        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<PaymentEvent>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    PaymentEvent(
                        sourcePackage = obj.optString("sourcePackage", "com.google.android.apps.nbu.paisa.user"),
                        provider = try { Provider.valueOf(obj.getString("provider")) } catch (e: Exception) { Provider.GENERIC },
                        direction = try { Direction.valueOf(obj.getString("direction")) } catch (e: Exception) { Direction.CREDIT },
                        amountMinor = obj.optLong("amountMinor", 100L),
                        payerName = if (obj.has("payerName") && !obj.isNull("payerName")) obj.getString("payerName") else null,
                        transactionReference = if (obj.has("transactionReference") && !obj.isNull("transactionReference")) obj.getString("transactionReference") else null,
                        eventTime = obj.optLong("eventTime", System.currentTimeMillis()),
                        sourceNotificationKey = obj.optString("sourceNotificationKey", ""),
                        confidence = obj.optDouble("confidence", 0.95).toFloat(),
                        rawSnippet = obj.optString("rawSnippet", "")
                    )
                )
            }
            list
        } catch (e: Exception) {
            Log.e("UpiSoundbox", "Error loading persisted payment history", e)
            emptyList()
        }
    }

    private fun saveToDisk(events: List<PaymentEvent>) {
        try {
            val array = JSONArray()
            for (event in events) {
                val obj = JSONObject().apply {
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
                }
                array.put(obj)
            }
            prefs.edit().putString(keyHistoryJson, array.toString()).apply()
            Log.d("UpiSoundbox", "Saved ${events.size} payment events to disk")
        } catch (e: Exception) {
            Log.e("UpiSoundbox", "Error saving payment history to disk", e)
        }
    }

    fun addEvent(event: PaymentEvent) {
        synchronized(lock) {
            val current = _history.value.toMutableList()
            current.add(0, event)
            if (current.size > 200) {
                current.removeAt(current.lastIndex)
            }
            _history.value = current
            saveToDisk(current)
        }
    }

    fun clearHistory() {
        synchronized(lock) {
            _history.value = emptyList()
            prefs.edit().remove(keyHistoryJson).apply()
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
