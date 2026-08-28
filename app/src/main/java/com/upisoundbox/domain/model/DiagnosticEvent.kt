package com.upisoundbox.domain.model

data class DiagnosticEvent(
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String,
    val provider: String? = null,
    val direction: String? = null,
    val amountPresent: Boolean = false,
    val confidence: Float? = null,
    val message: String,
    val isError: Boolean = false
)
