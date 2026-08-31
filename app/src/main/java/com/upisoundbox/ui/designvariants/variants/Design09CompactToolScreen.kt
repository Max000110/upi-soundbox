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
fun Design09CompactToolScreen(
    onOpenDesignSelector: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = UpiSoundboxApp.instance.container
    val palette = DesignVariantTheme.getPalette(DesignVariant.DESIGN_09)
    val history by container.historyRepository.history.collectAsState()
    val (todayTotalMinor, todayCount) = container.historyRepository.getTodayStats()
    val todayFormatted = "₹" + String.format(Locale.getDefault(), "%,.2f", todayTotalMinor / 100.0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .padding(10.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Tool Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "UTILITY TOOLBOX // 09",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = palette.secondary
            )
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = onOpenDesignSelector,
                shape = RoundedCornerShape(palette.buttonRadius)
            ) {
                Text("Design 09", style = MaterialTheme.typography.labelSmall, color = palette.primary)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Telemetry Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(palette.surface)
                .border(1.dp, palette.border)
                .padding(10.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("TOTAL COLLECTIONS", style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
                    Text(todayFormatted, style = MaterialTheme.typography.titleLarge, color = palette.primary)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("TRANSACTION COUNT", style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
                    Text("$todayCount Events", style = MaterialTheme.typography.titleLarge, color = palette.textPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                container.speechQueue.enqueue(
                    SpeechRequest(text = "One rupee received from AFZAL KASAM MANSURI.", language = "en")
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(palette.buttonRadius),
            colors = ButtonDefaults.buttonColors(containerColor = palette.primary)
        ) {
            Text("TEST AUDIO DISPATCH", style = MaterialTheme.typography.labelSmall, color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("EVENT LEDGER", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp), color = palette.textSecondary)

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn {
            items(history) { event ->
                val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(event.eventTime))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .background(palette.surface)
                        .border(1.dp, palette.border)
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(event.payerName ?: "UPI PAYER", style = MaterialTheme.typography.titleSmall, color = palette.textPrimary)
                            Text("$timeStr • ${event.provider.name}", style = MaterialTheme.typography.bodySmall, color = palette.textSecondary)
                        }
                        Text(event.amountMajorFormatted, style = MaterialTheme.typography.titleMedium, color = palette.success)
                    }
                }
            }
        }
    }
}
