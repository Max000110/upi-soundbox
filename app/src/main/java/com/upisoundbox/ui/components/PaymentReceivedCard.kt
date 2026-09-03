package com.upisoundbox.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upisoundbox.core.model.AnnouncementState
import com.upisoundbox.domain.model.PaymentEvent
import com.upisoundbox.ui.theme.SoundboxColors
import com.upisoundbox.ui.theme.SoundboxDimensions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PaymentReceivedCard(
    event: PaymentEvent?,
    isAnnouncing: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SoundboxDimensions.HeroRadius),
        colors = CardDefaults.cardColors(
            containerColor = SoundboxColors.Surface
        ),
        border = BorderStroke(1.dp, SoundboxColors.Border),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        if (event == null) {
            // Idle State: Soundbox Ready
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SoundRipple(active = false, size = 64.dp)
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "UPI SOUNDBOX READY",
                    style = MaterialTheme.typography.labelLarge,
                    color = SoundboxColors.PrimaryAccent,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Waiting for customer payment...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoundboxColors.SecondaryText
                )
            }
        } else {
            val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(event.eventTime))
            val payerName = event.payerName ?: "UPI Customer"

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PAYMENT RECEIVED",
                        style = MaterialTheme.typography.labelLarge,
                        color = SoundboxColors.PaymentSuccess,
                        fontWeight = FontWeight.Bold
                    )
                    SoundRipple(active = isAnnouncing, size = 44.dp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Dominant Hero Amount
                Text(
                    text = event.amountMajorFormatted,
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1.5).sp,
                    color = SoundboxColors.PrimaryText
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Received from $payerName",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SoundboxColors.PrimaryText
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${event.provider.displayName} • $timeStr",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoundboxColors.SecondaryText
                )

                Spacer(modifier = Modifier.height(16.dp))

                AnnouncementStatus(
                    state = if (isAnnouncing) AnnouncementState.ANNOUNCEMENT_STARTED else event.announcementState
                )
            }
        }
    }
}
