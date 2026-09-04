package com.upisoundbox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upisoundbox.UpiSoundboxApp
import com.upisoundbox.domain.model.SpeechRequest
import com.upisoundbox.speech.VoicePersona
import com.upisoundbox.ui.theme.PrimaryContainerM3
import com.upisoundbox.ui.theme.PrimaryM3
import com.upisoundbox.ui.theme.SecondaryM3
import com.upisoundbox.ui.theme.SemanticSuccess
import com.upisoundbox.ui.theme.SurfacePrimary
import com.upisoundbox.ui.theme.SurfaceSecondary
import com.upisoundbox.ui.theme.TextPrimary
import com.upisoundbox.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun VoiceScreen(modifier: Modifier = Modifier) {
    val container = UpiSoundboxApp.instance.container
    val settings by container.settingsRepository.settingsFlow.collectAsState(initial = null)
    val ttsStatus by container.ttsEngine.status.collectAsState()
    val scope = rememberCoroutineScope()

    val currentPersona = settings?.voicePersona ?: VoicePersona.COQUI_WARM_RETAIL_FEMALE
    val currentLang = settings?.language ?: "en"
    val speechRate = settings?.speechRate ?: 1.0f
    val volume = settings?.volume ?: 1.0f
    val announcePayer = settings?.announcePayerName ?: true
    val announceProvider = settings?.announceProviderName ?: true
    val isSecureScreen = settings?.isSecureScreenEnabled ?: true

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = "Voice & Sound Models",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary
        )
        Text(
            text = "Coqui & Echidna Neural Voice Architecture",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Status Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(PrimaryContainerM3)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ACTIVE VOICE MODEL",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        color = PrimaryM3
                    )
                    Text(
                        text = currentPersona.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SemanticSuccess)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = ttsStatus.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Choose Voice Persona (8 Models Available)",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Text(
            text = "Tap to select and hit Preview to hear on your speaker",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        // List of Voice Personas
        VoicePersona.entries.forEach { persona ->
            val isSelected = persona == currentPersona

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isSelected) PrimaryContainerM3.copy(alpha = 0.5f) else SurfacePrimary)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) PrimaryM3 else Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable {
                        scope.launch {
                            container.settingsRepository.updateVoicePersona(persona)
                        }
                    }
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .border(2.dp, if (isSelected) PrimaryM3 else Color.Gray, CircleShape)
                                .background(if (isSelected) PrimaryM3 else Color.Transparent)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = persona.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SecondaryM3.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = persona.badge,
                                style = MaterialTheme.typography.labelSmall,
                                color = SecondaryM3
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = persona.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = {
                                val sampleText = if (currentLang == "hi") persona.sampleTextHi else persona.sampleTextEn
                                container.speechQueue.enqueue(
                                    SpeechRequest(
                                        text = sampleText,
                                        language = currentLang,
                                        speechRate = speechRate * persona.rateMultiplier,
                                        speechPitch = persona.pitchMultiplier,
                                        requestedVolume = volume
                                    )
                                )
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("▶ Preview Voice", style = MaterialTheme.typography.labelSmall, color = PrimaryM3)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Pitch ${persona.pitchMultiplier}x • Rate ${persona.rateMultiplier}x",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Language Selector
        Text(text = "Announcement Language", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            FilterChip(
                selected = currentLang == "en",
                onClick = { scope.launch { container.settingsRepository.updateLanguage("en") } },
                label = { Text("English (India)") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryContainerM3)
            )
            Spacer(modifier = Modifier.width(10.dp))
            FilterChip(
                selected = currentLang == "hi",
                onClick = { scope.launch { container.settingsRepository.updateLanguage("hi") } },
                label = { Text("हिंदी (Hindi)") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryContainerM3)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        val speechRate = settings?.speechRate ?: 1.0f
        val volume = settings?.volume ?: 1.0f

        var localSpeechRate by remember(speechRate) { mutableFloatStateOf(speechRate) }
        var localVolume by remember(volume) { mutableFloatStateOf(volume) }

        // Speech Rate Slider
        Text(
            text = "Master Speed Adjustment: ${String.format(java.util.Locale.US, "%.1f", localSpeechRate)}x",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Slider(
            value = localSpeechRate,
            onValueChange = { localSpeechRate = it },
            onValueChangeFinished = {
                scope.launch { container.settingsRepository.updateSpeechRate(localSpeechRate) }
            },
            valueRange = 0.7f..1.5f,
            steps = 7,
            colors = SliderDefaults.colors(thumbColor = PrimaryM3, activeTrackColor = PrimaryM3)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Volume Slider
        Text(
            text = "Soundbox Speaker Volume: ${(localVolume * 100).toInt()}%",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Slider(
            value = localVolume,
            onValueChange = { localVolume = it },
            onValueChangeFinished = {
                scope.launch { container.settingsRepository.updateVolume(localVolume) }
            },
            valueRange = 0.2f..1.0f,
            steps = 7,
            colors = SliderDefaults.colors(thumbColor = PrimaryM3, activeTrackColor = PrimaryM3)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Announce Payer Name Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Announce Payer Name", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text(text = "Speak sender's name when available", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Switch(
                checked = announcePayer,
                onCheckedChange = { scope.launch { container.settingsRepository.updateAnnouncePayer(it) } },
                colors = SwitchDefaults.colors(checkedThumbColor = PrimaryM3, checkedTrackColor = PrimaryContainerM3)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Announce Provider Name Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Announce UPI App Name", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text(text = "Speak app source (e.g. on PhonePe)", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Switch(
                checked = announceProvider,
                onCheckedChange = { scope.launch { container.settingsRepository.updateAnnounceProvider(it) } },
                colors = SwitchDefaults.colors(checkedThumbColor = PrimaryM3, checkedTrackColor = PrimaryContainerM3)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Privacy & Security Card
        Text(text = "Screen Shield", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfacePrimary)
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Screen Recording Guard", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Text(
                        text = "Blocks screen capture and app preview leaks (FLAG_SECURE)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                Switch(
                    checked = isSecureScreen,
                    onCheckedChange = { scope.launch { container.settingsRepository.updateSecureScreen(it) } },
                    colors = SwitchDefaults.colors(checkedThumbColor = PrimaryM3, checkedTrackColor = PrimaryContainerM3)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Button(
            onClick = {
                val sampleText = if (currentLang == "hi") currentPersona.sampleTextHi else currentPersona.sampleTextEn
                container.speechQueue.enqueue(
                    SpeechRequest(
                        text = sampleText,
                        language = currentLang,
                        speechRate = speechRate * currentPersona.rateMultiplier,
                        speechPitch = currentPersona.pitchMultiplier,
                        requestedVolume = volume
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryM3)
        ) {
            Text("🔊 Test Selected Model Announcement", style = MaterialTheme.typography.labelLarge, color = Color.White)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
