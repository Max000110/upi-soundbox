package com.upisoundbox.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upisoundbox.UpiSoundboxApp
import com.upisoundbox.domain.model.PaymentEvent
import com.upisoundbox.ui.theme.SoundboxColors
import com.upisoundbox.ui.theme.SoundboxDimensions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(modifier: Modifier = Modifier) {
    val container = UpiSoundboxApp.instance.container
    val history by container.historyRepository.history.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }

    val (todayTotalMinor, todayCount) = container.historyRepository.getTodayStats()
    val todayFormatted = "₹" + String.format(Locale.getDefault(), "%,.2f", todayTotalMinor / 100.0)

    val groupedHistory = remember(history) {
        groupHistoryByDate(history)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SoundboxColors.Background)
            .padding(horizontal = SoundboxDimensions.ScreenPadding)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Payment History",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SoundboxColors.PrimaryText
                )
                Text(
                    text = "Merchant Transaction Ledger",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoundboxColors.SecondaryText
                )
            }
            if (history.isNotEmpty()) {
                OutlinedButton(
                    onClick = { showClearDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SoundboxColors.Error.copy(alpha = 0.5f))
                ) {
                    Text("Clear", color = SoundboxColors.Error, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Daily Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SoundboxColors.Surface),
            border = BorderStroke(1.dp, SoundboxColors.Border),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TODAY'S RECEIVED TOTAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoundboxColors.SecondaryText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = todayFormatted,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoundboxColors.PrimaryText
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SoundboxColors.PaymentSuccessContainer)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$todayCount Received",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = SoundboxColors.PaymentSuccess
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No transaction records",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = SoundboxColors.PrimaryText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Received UPI payments will be listed here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoundboxColors.SecondaryText
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                groupedHistory.forEach { (header, events) ->
                    item {
                        Text(
                            text = header,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoundboxColors.SecondaryText,
                            modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                        )
                    }

                    items(events, key = { it.id }) { event ->
                        val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(event.eventTime))
                        val payerName = event.payerName ?: "UPI Customer"
                        val initial = payerName.firstOrNull()?.uppercase() ?: "U"

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = SoundboxColors.Surface),
                            border = BorderStroke(1.dp, SoundboxColors.Border),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(SoundboxColors.PaymentSuccessContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initial,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = SoundboxColors.PaymentSuccess
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = payerName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SoundboxColors.PrimaryText
                                    )
                                    val refInfo = if (!event.transactionReference.isNullOrBlank()) {
                                        " • Ref: ${event.transactionReference}"
                                    } else ""
                                    Text(
                                        text = "${event.provider.displayName} • $timeStr$refInfo",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SoundboxColors.SecondaryText
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = event.amountMajorFormatted,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SoundboxColors.PrimaryText
                                    )
                                    Text(
                                        text = "✓ Announced",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = SoundboxColors.PaymentSuccess
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Delete Payment History?", fontWeight = FontWeight.Bold) },
            text = { Text("This will remove locally stored payment records from this device. Transaction totals will reset.") },
            confirmButton = {
                Button(
                    onClick = {
                        container.historyRepository.clearHistory()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SoundboxColors.Error)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = SoundboxColors.SecondaryText)
                }
            }
        )
    }
}

private fun groupHistoryByDate(events: List<PaymentEvent>): Map<String, List<PaymentEvent>> {
    val now = Calendar.getInstance()
    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val yesterdayStart = todayStart - (24 * 3600 * 1000L)

    val map = linkedMapOf<String, MutableList<PaymentEvent>>()

    for (event in events) {
        val group = when {
            event.eventTime >= todayStart -> "TODAY"
            event.eventTime >= yesterdayStart -> "YESTERDAY"
            else -> SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(event.eventTime)).uppercase()
        }
        map.getOrPut(group) { mutableListOf() }.add(event)
    }

    return map
}
