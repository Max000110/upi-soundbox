package com.upisoundbox.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upisoundbox.domain.model.PaymentEvent
import com.upisoundbox.ui.theme.SoundboxColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecentPaymentRow(
    event: PaymentEvent,
    modifier: Modifier = Modifier
) {
    val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(event.eventTime))
    val payerName = event.payerName ?: "UPI Customer"
    val initial = payerName.firstOrNull()?.uppercase() ?: "U"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SoundboxColors.Surface),
        border = BorderStroke(1.dp, SoundboxColors.Border),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar badge
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(SoundboxColors.PaymentSuccessContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = SoundboxColors.PaymentSuccess
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = payerName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = SoundboxColors.PrimaryText
                )
                Text(
                    text = "${event.provider.displayName} • $timeStr",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoundboxColors.SecondaryText
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = event.amountMajorFormatted,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoundboxColors.PrimaryText
                )
                Text(
                    text = "Announced",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoundboxColors.PaymentSuccess,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
