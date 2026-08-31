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
fun Design04DenseScreen(
    onOpenDesignSelector: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = UpiSoundboxApp.instance.container
    val palette = DesignVariantTheme.getPalette(DesignVariant.DESIGN_04)
    val history by container.historyRepository.history.collectAsState()
    val (todayTotalMinor, todayCount) = container.historyRepository.getTodayStats()
    val todayFormatted = "₹" + String.format(Locale.getDefault(), "%,.2f", todayTotalMinor / 100.0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .padding(12.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Operations Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("OPS DASHBOARD", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp), color = palette.secondary)
                Text("Real-Time Telemetry", style = MaterialTheme.typography.titleMedium, color = palette.textPrimary)
            }
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = onOpenDesignSelector,
                shape = RoundedCornerShape(palette.buttonRadius)
            ) {
                Text("Design 04", style = MaterialTheme.typography.labelSmall, color = palette.primary)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4-Up Dense Metric Tiles
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(palette.cardRadius))
                    .background(palette.surface)
                    .border(1.dp, palette.border, RoundedCornerShape(palette.cardRadius))
                    .padding(10.dp)
            ) {
                Column {
                    Text("TODAY REVENUE", style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
                    Text(todayFormatted, style = MaterialTheme.typography.titleLarge, color = palette.primary)
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(palette.cardRadius))
                    .background(palette.surface)
                    .border(1.dp, palette.border, RoundedCornerShape(palette.cardRadius))
                    .padding(10.dp)
            ) {
                Column {
                    Text("COUNT", style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
                    Text("$todayCount Txns", style = MaterialTheme.typography.titleLarge, color = palette.textPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(palette.cardRadius))
                    .background(palette.surface)
                    .border(1.dp, palette.border, RoundedCornerShape(palette.cardRadius))
                    .padding(10.dp)
            ) {
                Column {
                    Text("LISTENER SERVICE", style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
                    Text("CONNECTED", style = MaterialTheme.typography.titleMedium, color = palette.success)
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(palette.cardRadius))
                    .background(palette.surface)
                    .border(1.dp, palette.border, RoundedCornerShape(palette.cardRadius))
                    .padding(10.dp)
            ) {
                Column {
                    Text("TTS ENGINE", style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
                    Text("GOOGLE READY", style = MaterialTheme.typography.titleMedium, color = palette.secondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                container.speechQueue.enqueue(
                    SpeechRequest(text = "One rupee payment received.", language = "en")
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(palette.buttonRadius),
            colors = ButtonDefaults.buttonColors(containerColor = palette.primary)
        ) {
            Text("Trigger TTS Ping", style = MaterialTheme.typography.labelMedium, color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("EVENT FEED", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp), color = palette.textSecondary)

        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn {
            items(history) { event ->
                val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(event.eventTime))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .background(palette.surface)
                        .border(1.dp, palette.border, RoundedCornerShape(palette.cardRadius))
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("●", color = palette.success, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(event.payerName ?: "Customer", style = MaterialTheme.typography.titleSmall, color = palette.textPrimary)
                            Text("$timeStr | ${event.provider.name}", style = MaterialTheme.typography.bodySmall, color = palette.textSecondary)
                        }
                        Text(event.amountMajorFormatted, style = MaterialTheme.typography.titleMedium, color = palette.primary)
                    }
                }
            }
        }
    }
}
