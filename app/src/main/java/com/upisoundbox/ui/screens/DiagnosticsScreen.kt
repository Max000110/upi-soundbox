package com.upisoundbox.ui.screens

import android.content.Intent
import android.provider.Settings
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.upisoundbox.UpiSoundboxApp
import com.upisoundbox.battery.BatteryOptimizationHelper
import com.upisoundbox.domain.model.RawNotification
import com.upisoundbox.notification.NotificationAccessChecker
import com.upisoundbox.security.SecurityManager
import com.upisoundbox.ui.theme.AccentEvergreen
import com.upisoundbox.ui.theme.BorderColor
import com.upisoundbox.ui.theme.SemanticError
import com.upisoundbox.ui.theme.SemanticSuccess
import com.upisoundbox.ui.theme.SurfacePrimary
import com.upisoundbox.ui.theme.SurfaceSecondary
import com.upisoundbox.ui.theme.TextPrimary
import com.upisoundbox.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DiagnosticsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val container = UpiSoundboxApp.instance.container

    val listenerState by container.diagnosticsRepository.listenerState.collectAsState()
    val events by container.diagnosticsRepository.diagnosticEvents.collectAsState()
    val isAccessGranted = NotificationAccessChecker.isAccessGranted(context)
    val isDeviceRooted = SecurityManager.isDeviceRooted()
    val isBatteryUnrestricted = BatteryOptimizationHelper.isIgnoringBatteryOptimizations(context)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "System Diagnostics",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Diagnostic summary card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(SurfaceSecondary)
                .border(1.dp, BorderColor, MaterialTheme.shapes.medium)
                .padding(14.dp)
        ) {
            Column {
                DiagnosticStatusRow(
                    label = "Notification Permission",
                    value = if (isAccessGranted) "Granted" else "Not Granted",
                    isSuccess = isAccessGranted
                )
                Spacer(modifier = Modifier.height(8.dp))
                DiagnosticStatusRow(
                    label = "Listener Service",
                    value = listenerState.name,
                    isSuccess = listenerState.name == "CONNECTED"
                )
                Spacer(modifier = Modifier.height(8.dp))
                DiagnosticStatusRow(
                    label = "TTS Speech Engine",
                    value = if (container.ttsEngine.isAvailable()) "Ready" else "Initializing",
                    isSuccess = container.ttsEngine.isAvailable()
                )
                Spacer(modifier = Modifier.height(8.dp))
                DiagnosticStatusRow(
                    label = "Background Battery",
                    value = if (isBatteryUnrestricted) "Unrestricted (24/7)" else "Optimized (May Sleep)",
                    isSuccess = isBatteryUnrestricted
                )
                Spacer(modifier = Modifier.height(8.dp))
                DiagnosticStatusRow(
                    label = "Network Security",
                    value = "Air-Gapped (No Internet)",
                    isSuccess = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                DiagnosticStatusRow(
                    label = "Device Integrity",
                    value = if (isDeviceRooted) "Rooted Environment" else "Secure Sandbox",
                    isSuccess = !isDeviceRooted
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) },
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = AccentEvergreen)
            ) {
                Text("Notification Access", style = MaterialTheme.typography.labelMedium)
            }

            if (!isBatteryUnrestricted) {
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                OutlinedButton(
                    onClick = { BatteryOptimizationHelper.requestIgnoreBatteryOptimizations(context) },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Set Unrestricted", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Simulated Payment Test buttons
        Text(
            text = "Simulate Real Incoming Payment",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = {
                    container.processNotification(
                        RawNotification(
                            packageName = "com.phonepe.app",
                            notificationKey = "test_phonepe_${System.currentTimeMillis()}",
                            postedAt = System.currentTimeMillis(),
                            title = "Payment Received",
                            text = "You have received ₹500 from Rahul (UPI Ref: ${System.currentTimeMillis() % 1000000})"
                        )
                    )
                },
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.small
            ) {
                Text("PhonePe ₹500", style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.padding(horizontal = 4.dp))

            OutlinedButton(
                onClick = {
                    container.processNotification(
                        RawNotification(
                            packageName = "com.google.android.apps.nbu.paisa.user",
                            notificationKey = "test_gpay_${System.currentTimeMillis()}",
                            postedAt = System.currentTimeMillis(),
                            title = "Google Pay",
                            text = "Priya sent you ₹1,250.00 (UPI Ref: ${System.currentTimeMillis() % 1000000})"
                        )
                    )
                },
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.small
            ) {
                Text("GPay ₹1250", style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.padding(horizontal = 4.dp))

            OutlinedButton(
                onClick = {
                    container.processNotification(
                        RawNotification(
                            packageName = "net.one97.paytm",
                            notificationKey = "test_paytm_${System.currentTimeMillis()}",
                            postedAt = System.currentTimeMillis(),
                            title = "Payment Received",
                            text = "Received ₹2,000 from Amit (UPI Ref: ${System.currentTimeMillis() % 1000000})"
                        )
                    )
                },
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.small
            ) {
                Text("Paytm ₹2000", style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Live Diagnostic Events",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (events.isEmpty()) {
            Text(
                text = "No diagnostic events logged yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            LazyColumn {
                items(events) { ev ->
                    val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(ev.timestamp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(SurfacePrimary)
                            .border(1.dp, BorderColor, MaterialTheme.shapes.small)
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = ev.eventType,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = if (ev.isError) SemanticError else TextPrimary
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = timeStr,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = ev.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiagnosticStatusRow(label: String, value: String, isSuccess: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = if (isSuccess) SemanticSuccess else SemanticError
        )
    }
}
