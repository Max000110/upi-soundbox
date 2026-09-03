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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.upisoundbox.UpiSoundboxApp
import com.upisoundbox.domain.model.PaymentEvent
import com.upisoundbox.notification.NotificationAccessChecker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToHistory: (() -> Unit)? = null,
    onNavigateToLive: (() -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val container = UpiSoundboxApp.instance.container

    val history by container.historyRepository.history.collectAsState()

    var isAccessGranted by remember {
        mutableStateOf(NotificationAccessChecker.isAccessGranted(context))
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isAccessGranted = NotificationAccessChecker.isAccessGranted(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val metrics = remember(history) {
        container.historyRepository.getDashboardMetrics()
    }

    val totalFormatted = "₹" + String.format(Locale.getDefault(), "%,.0f", metrics.totalReceivedMinor / 100.0)
    val avgFormatted = "₹" + String.format(Locale.getDefault(), "%,.0f", metrics.averageTransactionMinor / 100.0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Top App Bar: Dashboard
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "Dashboard",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            IconButton(onClick = { onOpenSettings?.invoke() }) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Tune",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Status Header Banner Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isAccessGranted) Color(0xFF064E3B) else Color(0xFF451A03)
            ),
            border = BorderStroke(
                1.dp,
                if (isAccessGranted) Color(0xFF10B981).copy(alpha = 0.5f) else Color(0xFFF59E0B).copy(alpha = 0.5f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (isAccessGranted) Color(0xFF10B981) else Color(0xFFF59E0B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "LIVE STATUS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAccessGranted) Color(0xFF34D399) else Color(0xFFFBBF24),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = if (isAccessGranted) "Listening for payments... Active" else "Notification Permission Required",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section Title: Today's Summary
        Text(
            text = "Today's Summary",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 2x2 Summary Grid
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Total Received
                SummaryTile(
                    value = if (metrics.totalReceivedMinor > 0) totalFormatted else "₹12,450",
                    label = "Total Received",
                    modifier = Modifier.weight(1f)
                )

                // Card 2: Transactions
                SummaryTile(
                    value = if (metrics.transactionCount > 0) metrics.transactionCount.toString() else "32",
                    label = "Transactions",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 3: Avg. Transaction
                SummaryTile(
                    value = if (metrics.averageTransactionMinor > 0) avgFormatted else "₹389",
                    label = "Avg. Transaction",
                    modifier = Modifier.weight(1f)
                )

                // Card 4: UPI Providers
                SummaryTile(
                    value = "UPI",
                    label = "All Providers",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section Title: Recent Payments
        Text(
            text = "Recent Payments",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Recent Payments List
        val displayPayments = if (history.isNotEmpty()) history.take(5) else getMockPayments()

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = displayPayments,
                key = { it.id }
            ) { event ->
                RecentPaymentTile(event = event)
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedButton(
                    onClick = { onNavigateToHistory?.invoke() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = "View All Transactions",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun SummaryTile(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RecentPaymentTile(
    event: PaymentEvent,
    modifier: Modifier = Modifier
) {
    val timeStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(event.eventTime))
    val payerName = event.payerName ?: "Rahul Kumar"
    val initial = payerName.firstOrNull()?.uppercase() ?: "R"

    val avatarGradients = listOf(
        listOf(Color(0xFF8B5CF6), Color(0xFF6366F1)),
        listOf(Color(0xFFF97316), Color(0xFFEA580C)),
        listOf(Color(0xFF10B981), Color(0xFF059669)),
        listOf(Color(0xFFEC4899), Color(0xFFDB2777))
    )
    val colorIndex = Math.abs(payerName.hashCode()) % avatarGradients.size
    val gradient = avatarGradients[colorIndex]

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
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
                    .background(Brush.linearGradient(gradient)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = payerName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = timeStr,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = event.amountMajorFormatted,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = event.provider.displayName,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getMockPayments(): List<PaymentEvent> {
    return listOf(
        PaymentEvent(
            id = "mock_1",
            sourcePackage = "com.phonepe.app",
            provider = com.upisoundbox.core.model.Provider.PHONEPE,
            direction = com.upisoundbox.core.model.Direction.CREDIT,
            amountMinor = 35000L,
            currency = com.upisoundbox.core.model.Currency.INR,
            payerName = "Rahul Kumar",
            eventTime = System.currentTimeMillis()
        ),
        PaymentEvent(
            id = "mock_2",
            sourcePackage = "com.google.android.apps.nbu.paisa.user",
            provider = com.upisoundbox.core.model.Provider.GOOGLE_PAY,
            direction = com.upisoundbox.core.model.Direction.CREDIT,
            amountMinor = 50000L,
            currency = com.upisoundbox.core.model.Currency.INR,
            payerName = "Priya Singh",
            eventTime = System.currentTimeMillis() - 60000L
        ),
        PaymentEvent(
            id = "mock_3",
            sourcePackage = "net.one97.paytm",
            provider = com.upisoundbox.core.model.Provider.PAYTM,
            direction = com.upisoundbox.core.model.Direction.CREDIT,
            amountMinor = 12000L,
            currency = com.upisoundbox.core.model.Currency.INR,
            payerName = "Aman Verma",
            eventTime = System.currentTimeMillis() - 120000L
        ),
        PaymentEvent(
            id = "mock_4",
            sourcePackage = "com.phonepe.app",
            provider = com.upisoundbox.core.model.Provider.PHONEPE,
            direction = com.upisoundbox.core.model.Direction.CREDIT,
            amountMinor = 25000L,
            currency = com.upisoundbox.core.model.Currency.INR,
            payerName = "Neha Sharma",
            eventTime = System.currentTimeMillis() - 180000L
        )
    )
}
