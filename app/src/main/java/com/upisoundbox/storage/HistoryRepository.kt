package com.upisoundbox.storage

import com.upisoundbox.domain.model.PaymentEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

class HistoryRepository {

    private val _history = MutableStateFlow<List<PaymentEvent>>(emptyList())
    val history: StateFlow<List<PaymentEvent>> = _history.asStateFlow()

    private val lock = Any()

    fun addEvent(event: PaymentEvent) {
        synchronized(lock) {
            val current = _history.value.toMutableList()
            current.add(0, event)
            if (current.size > 100) {
                current.removeAt(current.lastIndex)
            }
            _history.value = current
        }
    }

    fun clearHistory() {
        synchronized(lock) {
            _history.value = emptyList()
        }
    }

    fun getTodayStats(): Pair<Long, Int> {
        val now = Calendar.getInstance()
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
