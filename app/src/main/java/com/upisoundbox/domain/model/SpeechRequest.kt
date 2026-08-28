package com.upisoundbox.domain.model

data class SpeechRequest(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val language: String = "en", // "en" or "hi"
    val speechRate: Float = 1.0f,
    val speechPitch: Float = 1.0f,
    val requestedVolume: Float = 1.0f,
    val boostVolume: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
