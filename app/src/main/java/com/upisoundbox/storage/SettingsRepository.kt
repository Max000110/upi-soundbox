package com.upisoundbox.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.upisoundbox.core.model.Provider
import com.upisoundbox.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "upi_soundbox_settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val LANGUAGE = stringPreferencesKey("language")
        val SPEECH_RATE = floatPreferencesKey("speech_rate")
        val SPEECH_PITCH = floatPreferencesKey("speech_pitch")
        val VOLUME = floatPreferencesKey("volume")
        val TEMP_VOLUME_BOOST = booleanPreferencesKey("temp_volume_boost")
        val ANNOUNCE_PAYER = booleanPreferencesKey("announce_payer")
        val ANNOUNCE_PROVIDER = booleanPreferencesKey("announce_provider")
        val DEDUP_WINDOW_SECONDS = intPreferencesKey("dedup_window_seconds")
        val ENABLED_PROVIDERS = stringSetPreferencesKey("enabled_providers")
        val HISTORY_ENABLED = booleanPreferencesKey("history_enabled")
        val SECURE_SCREEN = booleanPreferencesKey("secure_screen")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        val defaultProviders = Provider.entries.map { it.name }.toSet()
        UserSettings(
            language = prefs[Keys.LANGUAGE] ?: "en",
            speechRate = prefs[Keys.SPEECH_RATE] ?: 1.0f,
            speechPitch = prefs[Keys.SPEECH_PITCH] ?: 1.0f,
            volume = prefs[Keys.VOLUME] ?: 1.0f,
            temporaryVolumeBoost = prefs[Keys.TEMP_VOLUME_BOOST] ?: false,
            announcePayerName = prefs[Keys.ANNOUNCE_PAYER] ?: true,
            announceProviderName = prefs[Keys.ANNOUNCE_PROVIDER] ?: true,
            deduplicationWindowSeconds = prefs[Keys.DEDUP_WINDOW_SECONDS] ?: 60,
            enabledProviders = prefs[Keys.ENABLED_PROVIDERS] ?: defaultProviders,
            isHistoryEnabled = prefs[Keys.HISTORY_ENABLED] ?: true,
            isSecureScreenEnabled = prefs[Keys.SECURE_SCREEN] ?: true
        )
    }

    suspend fun updateLanguage(lang: String) {
        context.dataStore.edit { it[Keys.LANGUAGE] = lang }
    }

    suspend fun updateSpeechRate(rate: Float) {
        context.dataStore.edit { it[Keys.SPEECH_RATE] = rate }
    }

    suspend fun updateVolume(volume: Float) {
        context.dataStore.edit { it[Keys.VOLUME] = volume }
    }

    suspend fun updateAnnouncePayer(enabled: Boolean) {
        context.dataStore.edit { it[Keys.ANNOUNCE_PAYER] = enabled }
    }

    suspend fun updateAnnounceProvider(enabled: Boolean) {
        context.dataStore.edit { it[Keys.ANNOUNCE_PROVIDER] = enabled }
    }

    suspend fun updateSecureScreen(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SECURE_SCREEN] = enabled }
    }

    suspend fun toggleProvider(providerName: String, isEnabled: Boolean) {
        context.dataStore.edit { prefs ->
            val current = (prefs[Keys.ENABLED_PROVIDERS] ?: Provider.entries.map { it.name }.toSet()).toMutableSet()
            if (isEnabled) {
                current.add(providerName)
            } else {
                current.remove(providerName)
            }
            prefs[Keys.ENABLED_PROVIDERS] = current
        }
    }
}
