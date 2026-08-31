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
fun Design08LargeTypeScreen(
    onOpenDesignSelector: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = UpiSoundboxApp.instance.container
    val palette = DesignVariantTheme.getPalette(DesignVariant.DESIGN_08)
    val history by container.historyRepository.history.collectAsState()
    val (todayTotalMinor, todayCount) = container.historyRepository.getTodayStats()
    val todayFormatted = "₹" + String.format(Locale.getDefault(), "%,.2f", todayTotalMinor / 100.0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Large Type Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SOUNDBOX DUKAN",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                color = palette.primary
            )
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = onOpenDesignSelector,
                shape = RoundedCornerShape(palette.buttonRadius)
            ) {
                Text("Design 08", style = MaterialTheme.typography.labelMedium, color = palette.primary)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Big Counter Glanceable Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(palette.cardRadius))
                .background(palette.surface)
                .border(2.dp, palette.primary, RoundedCornerShape(palette.cardRadius))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "TODAY'S CASHLESS TOTAL",
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                    color = palette.textSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = todayFormatted,
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 44.sp),
                    color = palette.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$todayCount Payments Received",
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Extra Large Tactile Button
        Button(
            onClick = {
                container.speechQueue.enqueue(
                    SpeechRequest(text = "One rupee received on Google Pay.", language = "en")
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(palette.buttonRadius),
            colors = ButtonDefaults.buttonColors(containerColor = palette.primary)
        ) {
            Text("🔊 TEST SOUNDBOX VOICE", style = MaterialTheme.typography.titleMedium, color = Color.White)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "TODAY'S PAYMENTS",
            style = MaterialTheme.typography.titleMedium.copy(letterSpacing = 1.sp),
            color = palette.textPrimary
        )

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
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = event.payerName ?: "Customer",
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                                color = palette.textPrimary
                            )
                            Text(
                                text = "${event.provider.displayName} • $timeStr",
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.textSecondary
                            )
                        }
                        Text(
                            text = event.amountMajorFormatted,
                            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 26.sp),
                            color = palette.success
                        )
                    }
                }
            }
        }
    }
}
