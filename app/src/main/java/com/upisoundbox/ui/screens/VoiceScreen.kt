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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.upisoundbox.UpiSoundboxApp
import com.upisoundbox.domain.model.SpeechRequest
import com.upisoundbox.ui.theme.AccentEvergreen
import com.upisoundbox.ui.theme.BorderColor
import com.upisoundbox.ui.theme.SurfaceSecondary
import com.upisoundbox.ui.theme.TextPrimary
import com.upisoundbox.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun VoiceScreen(modifier: Modifier = Modifier) {
    val container = UpiSoundboxApp.instance.container
    val settings by container.settingsRepository.settingsFlow.collectAsState(initial = null)
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
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Voice & Settings",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Language Selection
        Text(text = "Announcement Language", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            FilterChip(
                selected = currentLang == "en",
                onClick = { scope.launch { container.settingsRepository.updateLanguage("en") } },
                label = { Text("English") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AccentEvergreen.copy(alpha = 0.2f))
            )
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            FilterChip(
                selected = currentLang == "hi",
                onClick = { scope.launch { container.settingsRepository.updateLanguage("hi") } },
                label = { Text("Hindi (हिंदी)") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AccentEvergreen.copy(alpha = 0.2f))
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Speech Rate Slider
        Text(
            text = "Speech Rate: ${String.format("%.1f", speechRate)}x",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )
        Slider(
            value = speechRate,
            onValueChange = { scope.launch { container.settingsRepository.updateSpeechRate(it) } },
            valueRange = 0.7f..1.5f,
            steps = 7,
            colors = SliderDefaults.colors(thumbColor = AccentEvergreen, activeTrackColor = AccentEvergreen)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Volume Slider
        Text(
            text = "Announcement Volume: ${(volume * 100).toInt()}%",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )
        Slider(
            value = volume,
            onValueChange = { scope.launch { container.settingsRepository.updateVolume(it) } },
            valueRange = 0.2f..1.0f,
            steps = 7,
            colors = SliderDefaults.colors(thumbColor = AccentEvergreen, activeTrackColor = AccentEvergreen)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Announce Payer Name Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Announce Payer Name", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                Text(text = "Speak who sent the payment when available", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
            Switch(
                checked = announcePayer,
                onCheckedChange = { scope.launch { container.settingsRepository.updateAnnouncePayer(it) } },
                colors = SwitchDefaults.colors(checkedThumbColor = AccentEvergreen, checkedTrackColor = AccentEvergreen.copy(alpha = 0.3f))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Announce Provider Name Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Announce App Name", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                Text(text = "Include app name (e.g. on PhonePe)", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
            Switch(
                checked = announceProvider,
                onCheckedChange = { scope.launch { container.settingsRepository.updateAnnounceProvider(it) } },
                colors = SwitchDefaults.colors(checkedThumbColor = AccentEvergreen, checkedTrackColor = AccentEvergreen.copy(alpha = 0.3f))
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Privacy & Security Card
        Text(text = "Privacy & Screen Shield", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(SurfaceSecondary)
                .border(1.dp, BorderColor, MaterialTheme.shapes.medium)
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Anti-Screen Recording Guard", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                    Text(
                        text = "Blocks screenshots, screen recording, and app preview snapshots (FLAG_SECURE)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                Switch(
                    checked = isSecureScreen,
                    onCheckedChange = { scope.launch { container.settingsRepository.updateSecureScreen(it) } },
                    colors = SwitchDefaults.colors(checkedThumbColor = AccentEvergreen, checkedTrackColor = AccentEvergreen.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Play Sample
        Button(
            onClick = {
                val sampleText = if (currentLang == "hi") {
                    "राहुल से पचास रुपये प्राप्त हुए।"
                } else {
                    "Received fifty rupees from Rahul."
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
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = AccentEvergreen)
        ) {
            Text("Play Sample Voice")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
