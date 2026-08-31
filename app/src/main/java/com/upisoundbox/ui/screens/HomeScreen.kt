package com.upisoundbox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.upisoundbox.UpiSoundboxApp
import com.upisoundbox.domain.model.SpeechRequest
import com.upisoundbox.notification.NotificationAccessChecker
import com.upisoundbox.ui.theme.PrimaryContainerM3
import com.upisoundbox.ui.theme.PrimaryM3
import com.upisoundbox.ui.theme.SemanticSuccess
import com.upisoundbox.ui.theme.SurfacePrimary
import com.upisoundbox.ui.theme.TextPrimary
import com.upisoundbox.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onOpenDesignSelector: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val container = UpiSoundboxApp.instance.container

    val history by container.historyRepository.history.collectAsState()
    val isAccessGranted = NotificationAccessChecker.isAccessGranted(context)
    val (todayTotalMinor, todayCount) = container.historyRepository.getTodayStats()
    val todayFormatted = "₹" + String.format(Locale.getDefault(), "%,.2f", todayTotalMinor / 100.0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Material 3 Expressive Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "UPI Soundbox",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary
                )
                Text(
                    text = "Design 05 • Material 3 Expressive",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (onOpenDesignSelector != null) {
                OutlinedButton(
                    onClick = onOpenDesignSelector,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Design 05", style = MaterialTheme.typography.labelSmall, color = PrimaryM3)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tonal Hero Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(PrimaryContainerM3)
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isAccessGranted) SemanticSuccess else PrimaryM3)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isAccessGranted) "LIVE & ARMED" else "ACTION REQUIRED",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Instant Voice Announcements",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = todayFormatted,
                    style = MaterialTheme.typography.displaySmall,
                    color = PrimaryM3
                )
                Text(
                    text = "$todayCount Received Transactions Today",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Speech Action Button
        Button(
            onClick = {
                container.speechQueue.enqueue(
                    SpeechRequest(
                        text = "Payment received. One rupee on Google Pay.",
                        language = "en"
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryM3)
        ) {
            Text("🔊 Test Soundbox Announcement", style = MaterialTheme.typography.labelLarge, color = Color.White)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Recent Transactions",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (history.isEmpty()) {
            Text(
                text = "No payments received yet today.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        } else {
            LazyColumn {
                items(history) { event ->
                    val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(event.eventTime))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfacePrimary)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = event.payerName ?: "UPI Customer",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${event.provider.displayName} • $timeStr",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            Text(
                                text = event.amountMajorFormatted,
                                style = MaterialTheme.typography.titleLarge,
                                color = PrimaryM3
                            )
                        }
                    }
                }
            }
        }
    }
}
