package com.upisoundbox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.upisoundbox.domain.model.PaymentEvent
import com.upisoundbox.ui.theme.BorderColor
import com.upisoundbox.ui.theme.SurfacePrimary
import com.upisoundbox.ui.theme.TextPrimary
import com.upisoundbox.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LastPaymentCard(
    event: PaymentEvent?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(SurfacePrimary)
            .border(1.dp, BorderColor, MaterialTheme.shapes.large)
            .padding(16.dp)
    ) {
        if (event == null) {
            Column {
                Text(
                    text = "Last payment",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "No payments received yet today.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        } else {
            val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(event.eventTime))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = event.amountMajorFormatted,
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = timeStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                val subtitle = if (!event.payerName.isNullOrBlank()) {
                    "Received from ${event.payerName} · ${event.provider.displayName}"
                } else {
                    "Received on ${event.provider.displayName}"
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}
