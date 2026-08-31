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
fun Design02MinimalScreen(
    onOpenDesignSelector: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = UpiSoundboxApp.instance.container
    val palette = DesignVariantTheme.getPalette(DesignVariant.DESIGN_02)
    val history by container.historyRepository.history.collectAsState()
    val (todayTotalMinor, todayCount) = container.historyRepository.getTodayStats()
    val todayFormatted = "₹" + String.format(Locale.getDefault(), "%,.2f", todayTotalMinor / 100.0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top Minimal Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CONSOLE / LIVE",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
                color = palette.textSecondary
            )
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = onOpenDesignSelector,
                shape = RoundedCornerShape(palette.buttonRadius)
            ) {
                Text("Design 02", style = MaterialTheme.typography.labelSmall, color = palette.textPrimary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Massive Single-Glance Status Hero Tile
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(palette.cardRadius))
                .background(palette.surface)
                .border(2.dp, palette.success, RoundedCornerShape(palette.cardRadius))
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(palette.success)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SYSTEM ARMED & LISTENING",
                        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                        color = palette.success
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = todayFormatted,
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 38.sp),
                    color = palette.textPrimary
                )
                Text(
                    text = "Total Collected Today ($todayCount Payments)",
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Row
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    container.speechQueue.enqueue(
                        SpeechRequest(text = "One rupee received from AFZAL KASAM MANSURI.", language = "en")
                    )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(palette.buttonRadius),
                colors = ButtonDefaults.buttonColors(containerColor = palette.primary)
            ) {
                Text("Test Audio", style = MaterialTheme.typography.labelMedium, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "LIVE PAYMENT LOG",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
            color = palette.textSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(history) { event ->
                val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(event.eventTime))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .background(palette.surface)
                        .border(1.dp, palette.border, RoundedCornerShape(palette.cardRadius))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = event.payerName ?: "Unknown Payer",
                                style = MaterialTheme.typography.titleMedium,
                                color = palette.textPrimary
                            )
                            Text(
                                text = "$timeStr • ${event.provider.displayName}",
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
