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
import com.upisoundbox.UpiSoundboxApp
import com.upisoundbox.domain.model.SpeechRequest
import com.upisoundbox.ui.designvariants.DesignVariant
import com.upisoundbox.ui.designvariants.DesignVariantTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Design06SoftPremiumScreen(
    onOpenDesignSelector: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = UpiSoundboxApp.instance.container
    val palette = DesignVariantTheme.getPalette(DesignVariant.DESIGN_06)
    val history by container.historyRepository.history.collectAsState()
    val (todayTotalMinor, todayCount) = container.historyRepository.getTodayStats()
    val todayFormatted = "₹" + String.format(Locale.getDefault(), "%,.2f", todayTotalMinor / 100.0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .padding(18.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Soft Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Soundbox Organica", style = MaterialTheme.typography.titleLarge, color = palette.textPrimary)
                Text("Warm Soft Aesthetic", style = MaterialTheme.typography.bodySmall, color = palette.textSecondary)
            }
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = onOpenDesignSelector,
                shape = RoundedCornerShape(palette.buttonRadius)
            ) {
                Text("Design 06", style = MaterialTheme.typography.labelSmall, color = palette.primary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Organic Hero
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(palette.cardRadius))
                .background(palette.surface)
                .border(1.dp, palette.border, RoundedCornerShape(palette.cardRadius))
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(palette.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔊", style = MaterialTheme.typography.titleLarge)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text("Ready & Listening", style = MaterialTheme.typography.titleMedium, color = palette.primary)
                    Text(todayFormatted, style = MaterialTheme.typography.headlineMedium, color = palette.textPrimary)
                    Text("$todayCount Transactions", style = MaterialTheme.typography.bodySmall, color = palette.textSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

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
            Text("Test Natural Voice", style = MaterialTheme.typography.labelMedium, color = Color.White)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text("Payment Records", style = MaterialTheme.typography.titleMedium, color = palette.textPrimary)

        Spacer(modifier = Modifier.height(8.dp))

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
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(event.payerName ?: "Customer", style = MaterialTheme.typography.titleMedium, color = palette.textPrimary)
                            Text("${event.provider.displayName} • $timeStr", style = MaterialTheme.typography.bodySmall, color = palette.textSecondary)
                        }
                        Text(event.amountMajorFormatted, style = MaterialTheme.typography.titleLarge, color = palette.primary)
                    }
                }
            }
        }
    }
}
