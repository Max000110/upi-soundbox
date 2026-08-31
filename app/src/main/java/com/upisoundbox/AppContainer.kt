package com.upisoundbox

import android.content.Context
import android.util.Log
import com.upisoundbox.dedupe.PaymentDeduplicator
import com.upisoundbox.diagnostics.DiagnosticsRepository
import com.upisoundbox.domain.model.DiagnosticEvent
import com.upisoundbox.domain.model.RawNotification
import com.upisoundbox.domain.model.SpeechRequest
import com.upisoundbox.domain.model.UserSettings
import com.upisoundbox.notification.NotificationNormalizer
import com.upisoundbox.parser.ParseResult
import com.upisoundbox.parser.ParserRegistry
import com.upisoundbox.speech.AndroidTtsEngine
import com.upisoundbox.speech.AnnouncementFormatter
import com.upisoundbox.speech.SpeechQueue
import com.upisoundbox.storage.HistoryRepository
import com.upisoundbox.storage.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AppContainer(val context: Context) {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val settingsRepository by lazy { SettingsRepository(context) }
    val historyRepository by lazy { HistoryRepository(context) }
    val diagnosticsRepository by lazy { DiagnosticsRepository() }
    val deduplicator by lazy { PaymentDeduplicator(60) }

    val ttsEngine by lazy { AndroidTtsEngine(context) }
    val speechQueue by lazy { SpeechQueue(ttsEngine, applicationScope) }

    fun processNotification(raw: RawNotification) {
        applicationScope.launch {
            try {
                val currentSettings: UserSettings = settingsRepository.settingsFlow.first()
                deduplicator.setWindowSeconds(currentSettings.deduplicationWindowSeconds)

                // 1. Normalize
                val normalized = NotificationNormalizer.normalize(raw)
                Log.d("UpiSoundbox", "Normalized text: '${normalized.canonicalText}'")

                // 2. Parse
                val parseResult = ParserRegistry.parse(normalized)
                Log.d("UpiSoundbox", "Parse result: $parseResult")

                when (parseResult) {
                    is ParseResult.Success -> {
                        val event = parseResult.event

                        // Check if provider is enabled
                        if (!currentSettings.isProviderEnabled(event.provider)) {
                            Log.w("UpiSoundbox", "Provider ${event.provider.displayName} is disabled in settings")
                            diagnosticsRepository.logDiagnostic(
                                DiagnosticEvent(
                                    eventType = "PROVIDER_DISABLED",
                                    provider = event.provider.displayName,
                                    message = "Ignored event from disabled provider: ${event.provider.displayName}"
                                )
                            )
                            return@launch
                        }

                        // 3. Deduplicate
                        if (deduplicator.isDuplicate(event)) {
                            Log.w("UpiSoundbox", "Duplicate payment suppressed: ${event.amountMajorFormatted}")
                            diagnosticsRepository.logDiagnostic(
                                DiagnosticEvent(
                                    eventType = "DUPLICATE_SUPPRESSED",
                                    provider = event.provider.displayName,
                                    message = "Duplicate event suppressed (${event.amountMajorFormatted})"
                                )
                            )
                            return@launch
                        }

                        // 4. Save to history
                        if (currentSettings.isHistoryEnabled) {
                            historyRepository.addEvent(event)
                        }

                        // 5. Update diagnostics
                        diagnosticsRepository.recordPaymentEvent(event)

                        // 6. Format announcement
                        val announcementText = AnnouncementFormatter.format(
                            event = event,
                            language = currentSettings.language,
                            includePayer = currentSettings.announcePayerName,
                            includeProvider = currentSettings.announceProviderName
                        )
                        Log.i("UpiSoundbox", "Announcement formatted: '$announcementText'")

                        // 7. Enqueue speech
                        val speechRequest = SpeechRequest(
                            text = announcementText,
                            language = currentSettings.language,
                            speechRate = currentSettings.speechRate,
                            speechPitch = currentSettings.speechPitch,
                            requestedVolume = currentSettings.volume,
                            boostVolume = currentSettings.temporaryVolumeBoost
                        )
                        Log.i("UpiSoundbox", "Enqueuing speech request: id=${speechRequest.id}")
                        speechQueue.enqueue(speechRequest)
                    }

                    is ParseResult.Ignored -> {
                        Log.d("UpiSoundbox", "Notification ignored: ${parseResult.reason}")
                    }

                    is ParseResult.Failed -> {
                        Log.w("UpiSoundbox", "Notification parse failed: ${parseResult.reason}")
                        diagnosticsRepository.logDiagnostic(
                            DiagnosticEvent(
                                eventType = "PARSE_FAILED",
                                message = "Parse failed for package ${raw.packageName}: ${parseResult.reason}"
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("UpiSoundbox", "Exception during notification processing", e)
                diagnosticsRepository.logDiagnostic(
                    DiagnosticEvent(
                        eventType = "ERROR",
                        message = "Exception during notification processing: ${e.localizedMessage}",
                        isError = true
                    )
                )
            }
        }
    }
}
