package com.upisoundbox.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upisoundbox.UpiSoundboxApp
import com.upisoundbox.domain.model.SpeechRequest
import com.upisoundbox.notification.NotificationAccessChecker
import com.upisoundbox.ui.components.PaymentReceivedCard
import com.upisoundbox.ui.components.RecentPaymentRow
import com.upisoundbox.ui.components.SoundboxStatusCard
import com.upisoundbox.ui.theme.SoundboxColors
import com.upisoundbox.ui.theme.SoundboxDimensions
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

    val latestEvent = history.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SoundboxColors.Background)
            .padding(horizontal = SoundboxDimensions.ScreenPadding)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "UPI Soundbox",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SoundboxColors.PrimaryText
                )
                Text(
                    text = "Live payment announcements",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoundboxColors.SecondaryText
                )
            }

            // Subtle Status Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isAccessGranted) SoundboxColors.PaymentSuccessContainer else SoundboxColors.WarningContainer)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (isAccessGranted) SoundboxColors.PaymentSuccess else SoundboxColors.Warning)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isAccessGranted) "Listening" else "Setup Needed",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isAccessGranted) SoundboxColors.PaymentSuccess else SoundboxColors.Warning
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Daily Metric Summary Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SoundboxColors.Surface)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Total: $todayFormatted",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = SoundboxColors.PrimaryText
            )
            Text(
                text = "$todayCount Transactions",
                fontSize = 13.sp,
                color = SoundboxColors.SecondaryText
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hero Payment Received Card
        PaymentReceivedCard(
            event = latestEvent,
            isAnnouncing = false
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Soundbox Status & Test Button
        SoundboxStatusCard(
            isListening = isAccessGranted,
            isVoiceReady = true,
            onTestAnnouncement = {
                container.speechQueue.enqueue(
                    SpeechRequest(
                        text = "PhonePe पर Rahul से पाँच सौ रुपये प्राप्त हुए।",
                        language = "hi"
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Recent Payments Header
        Text(
            text = "Recent Payments",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = SoundboxColors.PrimaryText
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No payments received yet today.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoundboxColors.SecondaryText
                )
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(
                    items = history,
                    key = { it.id }
                ) { event ->
                    RecentPaymentRow(event = event)
                }
            }
        }
    }
}
