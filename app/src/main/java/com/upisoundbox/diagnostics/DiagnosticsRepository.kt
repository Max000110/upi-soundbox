package com.upisoundbox.diagnostics

import com.upisoundbox.core.model.ListenerState
import com.upisoundbox.domain.model.DiagnosticEvent
import com.upisoundbox.domain.model.PaymentEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DiagnosticsRepository {

    private val _listenerState = MutableStateFlow(ListenerState.NOT_GRANTED)
    val listenerState: StateFlow<ListenerState> = _listenerState.asStateFlow()

    private val _lastEvent = MutableStateFlow<PaymentEvent?>(null)
    val lastEvent: StateFlow<PaymentEvent?> = _lastEvent.asStateFlow()

    private val _diagnosticEvents = MutableStateFlow<List<DiagnosticEvent>>(emptyList())
    val diagnosticEvents: StateFlow<List<DiagnosticEvent>> = _diagnosticEvents.asStateFlow()

    private val lock = Any()

    fun setListenerState(state: ListenerState) {
        _listenerState.value = state
        logDiagnostic(
            DiagnosticEvent(
                eventType = "LISTENER_STATE_CHANGED",
                message = "Notification Listener state changed to: $state"
            )
        )
    }

    fun recordPaymentEvent(event: PaymentEvent) {
        _lastEvent.value = event
        logDiagnostic(
            DiagnosticEvent(
                eventType = "PAYMENT_ANNOUNCED",
                provider = event.provider.displayName,
                direction = event.direction.name,
                amountPresent = true,
                confidence = event.confidence,
                message = "Announced ${event.amountMajorFormatted} from ${event.payerName ?: "Unknown"} via ${event.provider.displayName}"
            )
        )
    }

    fun logDiagnostic(event: DiagnosticEvent) {
        synchronized(lock) {
            val list = _diagnosticEvents.value.toMutableList()
            list.add(0, event)
            if (list.size > 50) {
                list.removeAt(list.lastIndex)
            }
            _diagnosticEvents.value = list
        }
    }
}
