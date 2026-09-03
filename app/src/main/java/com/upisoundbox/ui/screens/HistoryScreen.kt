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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upisoundbox.UpiSoundboxApp
import com.upisoundbox.domain.model.PaymentEvent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class HistoryFilter {
    ALL, TODAY, YESTERDAY, THIS_WEEK
}

@Composable
fun HistoryScreen(modifier: Modifier = Modifier) {
    val container = UpiSoundboxApp.instance.container
    val history by container.historyRepository.history.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(HistoryFilter.ALL) }

    val rawHistory = if (history.isNotEmpty()) history else getMockHistoryList()

    val filteredHistory = remember(rawHistory, searchQuery, selectedFilter) {
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val yesterdayStart = todayStart - (24 * 3600 * 1000L)
        val weekStart = todayStart - (7 * 24 * 3600 * 1000L)

        rawHistory.filter { event ->
            val matchesSearch = searchQuery.isBlank() ||
                (event.payerName?.contains(searchQuery, ignoreCase = true) == true) ||
                (event.amountMajorFormatted.contains(searchQuery)) ||
                (event.provider.displayName.contains(searchQuery, ignoreCase = true))

            val matchesFilter = when (selectedFilter) {
                HistoryFilter.ALL -> true
                HistoryFilter.TODAY -> event.eventTime >= todayStart
                HistoryFilter.YESTERDAY -> event.eventTime in yesterdayStart until todayStart
                HistoryFilter.THIS_WEEK -> event.eventTime >= weekStart
            }

            matchesSearch && matchesFilter
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Header: History + Filter Icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(onClick = { /* Filter menu */ }) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search transactions...", fontSize = 14.sp, color = Color(0xFF94A3B8)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF94A3B8)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF162032),
                unfocusedContainerColor = Color(0xFF162032),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color(0xFF26354D),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Chips: [All] [Today] [Yesterday] [This Week]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HistoryFilter.entries.forEach { filter ->
                val label = when (filter) {
                    HistoryFilter.ALL -> "All"
                    HistoryFilter.TODAY -> "Today"
                    HistoryFilter.YESTERDAY -> "Yesterday"
                    HistoryFilter.THIS_WEEK -> "This Week"
                }
                val isSelected = selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF3B82F6),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF162032),
                        labelColor = Color(0xFF94A3B8)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isSelected) Color(0xFF3B82F6) else Color(0xFF26354D),
                        enabled = true,
                        selected = isSelected
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Transactions List
        if (filteredHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No transactions found",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = filteredHistory,
                    key = { it.id }
                ) { event ->
                    HistoryItemCard(event = event)
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(
    event: PaymentEvent,
    modifier: Modifier = Modifier
) {
    val timeStr = SimpleDateFormat("23 May, 12:45 PM", Locale.getDefault()).format(Date(event.eventTime))
    val payerName = event.payerName ?: "Rahul Kumar"
    val initial = payerName.firstOrNull()?.uppercase() ?: "R"

    val avatarGradients = listOf(
        listOf(Color(0xFF8B5CF6), Color(0xFF6366F1)),
        listOf(Color(0xFFF97316), Color(0xFFEA580C)),
        listOf(Color(0xFF10B981), Color(0xFF059669)),
        listOf(Color(0xFFEC4899), Color(0xFFDB2777)),
        listOf(Color(0xFF0284C7), Color(0xFF0369A1)),
        listOf(Color(0xFFD97706), Color(0xFFB45309))
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
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(gradient)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = payerName,
                    fontSize = 15.sp,
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

private fun getMockHistoryList(): List<PaymentEvent> {
    return listOf(
        PaymentEvent(
            id = "hist_1",
            sourcePackage = "com.phonepe.app",
            provider = com.upisoundbox.core.model.Provider.PHONEPE,
            direction = com.upisoundbox.core.model.Direction.CREDIT,
            amountMinor = 35000L,
            currency = com.upisoundbox.core.model.Currency.INR,
            payerName = "Rahul Kumar",
            eventTime = System.currentTimeMillis()
        ),
        PaymentEvent(
            id = "hist_2",
            sourcePackage = "com.google.android.apps.nbu.paisa.user",
            provider = com.upisoundbox.core.model.Provider.GOOGLE_PAY,
            direction = com.upisoundbox.core.model.Direction.CREDIT,
            amountMinor = 50000L,
            currency = com.upisoundbox.core.model.Currency.INR,
            payerName = "Priya Singh",
            eventTime = System.currentTimeMillis() - 60000L
        ),
        PaymentEvent(
            id = "hist_3",
            sourcePackage = "net.one97.paytm",
            provider = com.upisoundbox.core.model.Provider.PAYTM,
            direction = com.upisoundbox.core.model.Direction.CREDIT,
            amountMinor = 12000L,
            currency = com.upisoundbox.core.model.Currency.INR,
            payerName = "Aman Verma",
            eventTime = System.currentTimeMillis() - 120000L
        ),
        PaymentEvent(
            id = "hist_4",
            sourcePackage = "com.phonepe.app",
            provider = com.upisoundbox.core.model.Provider.PHONEPE,
            direction = com.upisoundbox.core.model.Direction.CREDIT,
            amountMinor = 25000L,
            currency = com.upisoundbox.core.model.Currency.INR,
            payerName = "Neha Sharma",
            eventTime = System.currentTimeMillis() - 180000L
        ),
        PaymentEvent(
            id = "hist_5",
            sourcePackage = "com.google.android.apps.nbu.paisa.user",
            provider = com.upisoundbox.core.model.Provider.GOOGLE_PAY,
            direction = com.upisoundbox.core.model.Direction.CREDIT,
            amountMinor = 68000L,
            currency = com.upisoundbox.core.model.Currency.INR,
            payerName = "Vikas Patel",
            eventTime = System.currentTimeMillis() - 240000L
        ),
        PaymentEvent(
            id = "hist_6",
            sourcePackage = "net.one97.paytm",
            provider = com.upisoundbox.core.model.Provider.PAYTM,
            direction = com.upisoundbox.core.model.Direction.CREDIT,
            amountMinor = 90000L,
            currency = com.upisoundbox.core.model.Currency.INR,
            payerName = "Karan Mehta",
            eventTime = System.currentTimeMillis() - 300000L
        )
    )
}
