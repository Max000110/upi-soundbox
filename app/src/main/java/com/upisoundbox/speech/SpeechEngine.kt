package com.upisoundbox.speech

import com.upisoundbox.domain.model.SpeechRequest

interface SpeechEngine {
    fun isAvailable(): Boolean
    suspend fun speak(request: SpeechRequest): Boolean
    fun stop()
}
