package com.upisoundbox.domain.model

import com.upisoundbox.core.model.Provider
import com.upisoundbox.speech.VoicePersona

data class UserSettings(
    val language: String = "en",
    val speechRate: Float = 1.0f,
    val speechPitch: Float = 1.0f,
    val volume: Float = 1.0f,
    val temporaryVolumeBoost: Boolean = false,
    val announcePayerName: Boolean = true,
    val announceProviderName: Boolean = true,
    val deduplicationWindowSeconds: Int = 60,
    val enabledProviders: Set<String> = Provider.entries.map { it.name }.toSet(),
    val isHistoryEnabled: Boolean = true,
    val isSecureScreenEnabled: Boolean = true,
    val voicePersona: VoicePersona = VoicePersona.COQUI_WARM_RETAIL_FEMALE
) {
    fun isProviderEnabled(provider: Provider): Boolean {
        return enabledProviders.contains(provider.name)
    }
}
