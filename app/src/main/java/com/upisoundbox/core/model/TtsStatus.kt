package com.upisoundbox.core.model

enum class TtsStatus(val displayName: String) {
    UNINITIALIZED("Uninitialized"),
    INITIALIZING("Initializing"),
    READY("Ready"),
    SPEAKING("Speaking"),
    RETRYING("Retrying"),
    ERROR("Error"),
    UNAVAILABLE("Unavailable")
}
