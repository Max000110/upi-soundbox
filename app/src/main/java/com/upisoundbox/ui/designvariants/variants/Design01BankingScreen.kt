package com.upisoundbox.ui.designvariants.variants

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upisoundbox.UpiSoundboxApp
import com.upisoundbox.domain.model.SpeechRequest
import com.upisoundbox.ui.designvariants.DesignVariant
import com.upisoundbox.ui.designvariants.DesignVariantTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Design01BankingScreen(
    onOpenDesignSelector: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = UpiSoundboxApp.instance.container
    val palette = DesignVariantTheme.getPalette(DesignVariant.DESIGN_01)
    val history by container.historyRepository.history.collectAsState()
    val (todayTotalMinor, todayCount) = container.historyRepository.getTodayStats()
    val todayFormatted = "₹" + String.format(Locale.getDefault(), "%,.2f", todayTotalMinor / 100.0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "UPI SOUNDBOX PRO",
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                    color = palette.secondary
                )
                Text(
                    text = "Merchant Terminal",
                    style = MaterialTheme.typography.titleLarge,
                    color = palette.textPrimary
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = onOpenDesignSelector,
                shape = RoundedCornerShape(palette.buttonRadius)
            ) {
                Text("Design 01", style = MaterialTheme.typography.labelSmall, color = palette.primary)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Large Readiness Hero
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(palette.cardRadius))
                .background(palette.primary)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF69F0AE))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "LIVE SOUNDBOX ACTIVE",
                        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 0.5.sp),
                        color = Color(0xFFE0F2F1)
                    )
                    Text(
                        text = "Ready to Announce Payments",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Analytics Row
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(palette.cardRadius))
                    .background(palette.surface)
                    .border(1.dp, palette.border, RoundedCornerShape(palette.cardRadius))
                    .padding(14.dp)
            ) {
                Column {
                    Text("Today's Collections", style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(todayFormatted, style = MaterialTheme.typography.headlineSmall, color = palette.primary)
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(palette.cardRadius))
                    .background(palette.surface)
                    .border(1.dp, palette.border, RoundedCornerShape(palette.cardRadius))
                    .padding(14.dp)
            ) {
                Column {
                    Text("Transactions", style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$todayCount Received", style = MaterialTheme.typography.headlineSmall, color = palette.textPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Speech Action
        Button(
            onClick = {
                container.speechQueue.enqueue(
                    SpeechRequest(
                        text = "Payment received. Five hundred rupees on Google Pay.",
                        language = "en"
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(palette.buttonRadius),
            colors = ButtonDefaults.buttonColors(containerColor = palette.secondary)
        ) {
            Text("🔊 Test Announcement (Speak)", style = MaterialTheme.typography.labelLarge, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Recent Payments
        Text(
            text = "Recent Transactions",
            style = MaterialTheme.typography.titleMedium,
            color = palette.textPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (history.isEmpty()) {
            Text("No transactions received today.", style = MaterialTheme.typography.bodyMedium, color = palette.textSecondary)
        } else {
            LazyColumn {
                items(history) { event ->
                    val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(event.eventTime))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(palette.cardRadius))
                            .background(palette.surface)
                            .border(1.dp, palette.border, RoundedCornerShape(palette.cardRadius))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = event.payerName ?: "UPI Customer",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = palette.textPrimary
                                )
                                Text(
                                    text = "${event.provider.displayName} • $timeStr",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = palette.textSecondary
                                )
                            }
                            Text(
                                text = event.amountMajorFormatted,
                                style = MaterialTheme.typography.titleLarge,
                                color = palette.success
                            )
                        }
                    }
                }
            }
        }
    }
}
