package com.upisoundbox.speech

import com.upisoundbox.core.model.TtsStatus
import com.upisoundbox.domain.model.SpeechRequest
import kotlinx.coroutines.flow.StateFlow

interface SpeechEngine {
    val status: StateFlow<TtsStatus>
    fun isAvailable(): Boolean
    suspend fun speak(request: SpeechRequest): Boolean
    fun stop()
}
