package com.upisoundbox.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upisoundbox.UpiSoundboxApp
import com.upisoundbox.core.model.TtsStatus
import com.upisoundbox.domain.model.SpeechRequest
import com.upisoundbox.ui.components.SoundwaveVisualizer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LivePaymentScreen(
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val container = UpiSoundboxApp.instance.container
    val history by container.historyRepository.history.collectAsState()
    val ttsStatus by container.ttsEngine.status.collectAsState()
    val isSpeaking = ttsStatus == TtsStatus.SPEAKING

    val latestEvent = history.firstOrNull()
    var isTestActive by remember { mutableStateOf(false) }

    LaunchedEffect(ttsStatus) {
        if (ttsStatus == TtsStatus.READY && isTestActive) {
            isTestActive = false
        }
    }

    val amountText = latestEvent?.amountMajorFormatted ?: "₹350"
    val payerText = latestEvent?.payerName ?: "Rahul Kumar"
    val providerText = latestEvent?.provider?.displayName ?: "PhonePe"
    val timeText = if (latestEvent != null) {
        SimpleDateFormat("hh:mm a | dd MMM yyyy", Locale.getDefault()).format(Date(latestEvent.eventTime))
    } else {
        "12:45 PM | 23 May 2025"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onNavigateBack?.invoke() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Live Payment",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Forest Green Container Card (Exact from Reference Mockup)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF064E3B)
            ),
            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large Emerald Green Checkmark Circle with White Icon
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Payment Received",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFA7F3D0)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Huge 54sp Bold White Amount
                Text(
                    text = amountText,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1.5).sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "From",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )

                Text(
                    text = payerText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "via",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )

                Text(
                    text = providerText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF38BDF8)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Timestamp Pill Chip Container (Dark Emerald #065F46)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF065F46))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = timeText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFA7F3D0)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Audio Waveform Visualizer
                SoundwaveVisualizer(
                    isPlaying = isTestActive || isSpeaking,
                    height = 54.dp,
                    barCount = 30
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isTestActive || isSpeaking) "Playing announcement..." else "Voice announcement ready",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF38BDF8)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Test Announcement Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Test Announcement",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Switch(
                        checked = isTestActive,
                        onCheckedChange = { checked ->
                            isTestActive = checked
                            if (checked) {
                                container.speechQueue.enqueue(
                                    SpeechRequest(
                                        text = "$providerText पर $payerText से साढ़े तीन सौ रुपये प्राप्त हुए।",
                                        language = "hi"
                                    )
                                )
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF10B981),
                            checkedTrackColor = Color(0xFF065F46),
                            uncheckedThumbColor = Color(0xFF64748B),
                            uncheckedTrackColor = Color(0xFF1E293B)
                        )
                    )
                }
            }
        }
    }
}
