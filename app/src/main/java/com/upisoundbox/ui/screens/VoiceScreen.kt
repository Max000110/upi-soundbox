package com.upisoundbox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upisoundbox.UpiSoundboxApp
import com.upisoundbox.domain.model.SpeechRequest
import com.upisoundbox.ui.theme.PrimaryContainerM3
import com.upisoundbox.ui.theme.PrimaryM3
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
            text = "Voice & Soundbox",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary
        )
        Text(
            text = "High-Fidelity Offline Speech Synthesis",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TTS Engine Status Banner
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
                        text = "SPEECH SYNTHESIZER",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        color = PrimaryM3
                    )
                    Text(
                        text = "Google High-Quality Acoustic Voice",
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

        // Speech Rate Slider
        Text(
            text = "Speech Rate: ${String.format("%.1f", speechRate)}x",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Slider(
            value = speechRate,
            onValueChange = { scope.launch { container.settingsRepository.updateSpeechRate(it) } },
            valueRange = 0.7f..1.5f,
            steps = 7,
            colors = SliderDefaults.colors(thumbColor = PrimaryM3, activeTrackColor = PrimaryM3)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Volume Slider
        Text(
            text = "Announcement Volume: ${(volume * 100).toInt()}%",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Slider(
            value = volume,
            onValueChange = { scope.launch { container.settingsRepository.updateVolume(it) } },
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
        Text(text = "Screen Security", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
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

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Button(
            onClick = {
                val sampleText = if (currentLang == "hi") {
                    "गूगल पे पर, राहुल से पचास रुपये प्राप्त हुए।"
                } else {
                    "Received fifty rupees from Rahul, on Google Pay."
                }
                container.speechQueue.enqueue(
                    SpeechRequest(
                        text = sampleText,
                        language = currentLang,
                        speechRate = speechRate,
                        requestedVolume = volume
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryM3)
        ) {
            Text("🔊 Test Full Voice Announcement", style = MaterialTheme.typography.labelLarge, color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                container.ttsEngine.reinitialize()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("🔄 Re-warm Speech Engine", style = MaterialTheme.typography.labelMedium, color = PrimaryM3)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
