package com.upisoundbox.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.upisoundbox.UpiSoundboxApp
import com.upisoundbox.battery.BatteryOptimizationHelper
import com.upisoundbox.core.model.ListenerState
import com.upisoundbox.domain.model.SpeechRequest
import com.upisoundbox.notification.NotificationAccessChecker
import com.upisoundbox.ui.components.LastPaymentCard
import com.upisoundbox.ui.components.PaymentSummaryCard
import com.upisoundbox.ui.components.StatusCard
import com.upisoundbox.ui.components.StatusLevel
import com.upisoundbox.ui.theme.AccentEvergreen
import com.upisoundbox.ui.theme.TextPrimary
import java.util.Calendar

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val container = UpiSoundboxApp.instance.container

    val listenerState by container.diagnosticsRepository.listenerState.collectAsState()
    val lastEvent by container.diagnosticsRepository.lastEvent.collectAsState()
    val history by container.historyRepository.history.collectAsState()
    val settings by container.settingsRepository.settingsFlow.collectAsState(initial = null)

    var isAccessGranted by remember { mutableStateOf(NotificationAccessChecker.isAccessGranted(context)) }
    var isBatteryUnrestricted by remember { mutableStateOf(BatteryOptimizationHelper.isIgnoringBatteryOptimizations(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isAccessGranted = NotificationAccessChecker.isAccessGranted(context)
                isBatteryUnrestricted = BatteryOptimizationHelper.isIgnoringBatteryOptimizations(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Reactive computation of Today's stats from StateFlow
    val startOfDay = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    val todayEvents = history.filter { it.eventTime >= startOfDay }
    val todayTotal = todayEvents.sumOf { it.amountMinor }
    val todayCount = todayEvents.size
    val activeLastPayment = history.firstOrNull() ?: lastEvent

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "UPI Voice Soundbox",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // System Readiness Status
        if (!isAccessGranted) {
            StatusCard(
                level = StatusLevel.BLOCKED,
                title = "Notification Access Required",
                subtitle = "Enable notification access so UPI Soundbox can detect and announce incoming payments.",
                actionText = "Open Notification Settings",
                onActionClick = {
                    context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                }
            )
        } else if (listenerState == ListenerState.DISCONNECTED) {
            StatusCard(
                level = StatusLevel.ATTENTION,
                title = "Listener Connecting",
                subtitle = "Notification access is enabled. Waiting for Android to bind the listener service."
            )
        } else {
            StatusCard(
                level = StatusLevel.READY,
                title = "Soundbox Ready",
                subtitle = "Listening for supported payment notifications."
            )
        }

        // Unrestricted Battery Optimization Banner
        if (!isBatteryUnrestricted) {
            Spacer(modifier = Modifier.height(12.dp))
            StatusCard(
                level = StatusLevel.ATTENTION,
                title = "Set Background to Unrestricted",
                subtitle = "Allow unrestricted background battery usage so Android doesn't put the soundbox to sleep.",
                actionText = "Allow Unrestricted",
                onActionClick = {
                    BatteryOptimizationHelper.requestIgnoreBatteryOptimizations(context)
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Test Announcement Button
        Button(
            onClick = {
                val lang = settings?.language ?: "en"
                val sampleText = if (lang.equals("hi", ignoreCase = true)) {
                    "फोनपे पर पांच सौ रुपये प्राप्त हुए।"
                } else {
                    "Payment received. Five hundred rupees."
                }
                container.speechQueue.enqueue(
                    SpeechRequest(
                        text = sampleText,
                        language = lang,
                        speechRate = settings?.speechRate ?: 1.0f,
                        speechPitch = settings?.speechPitch ?: 1.0f,
                        requestedVolume = settings?.volume ?: 1.0f
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = AccentEvergreen)
        ) {
            Text(text = "Test Announcement (Speak)", modifier = Modifier.padding(vertical = 4.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Today's summary
        PaymentSummaryCard(
            totalAmountMinor = todayTotal,
            totalCount = todayCount
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Last Payment
        LastPaymentCard(event = activeLastPayment)

        Spacer(modifier = Modifier.height(32.dp))
    }
}
