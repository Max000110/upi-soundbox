package com.upisoundbox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.upisoundbox.ui.theme.BorderColor
import com.upisoundbox.ui.theme.SurfaceSecondary
import com.upisoundbox.ui.theme.TextPrimary
import com.upisoundbox.ui.theme.TextSecondary

@Composable
fun PaymentSummaryCard(
    totalAmountMinor: Long,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    val rupees = totalAmountMinor / 100
    val paise = totalAmountMinor % 100
    val formatted = if (paise == 0L) "₹$rupees" else "₹$rupees.${paise.toString().padStart(2, '0')}"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(SurfaceSecondary)
            .border(1.dp, BorderColor, MaterialTheme.shapes.large)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Today's collection",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatted,
                style = MaterialTheme.typography.displayLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (totalCount == 1) "1 payment received" else "$totalCount payments received",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}
