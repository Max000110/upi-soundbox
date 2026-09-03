package com.upisoundbox.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upisoundbox.UpiSoundboxApp
import com.upisoundbox.notification.NotificationAccessChecker
import com.upisoundbox.ui.theme.SoundboxColors
import com.upisoundbox.ui.theme.SoundboxDimensions
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val container = UpiSoundboxApp.instance.container
    val settings by container.settingsRepository.settingsFlow.collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    val isAccessGranted = NotificationAccessChecker.isAccessGranted(context)
    val isSecureScreen = settings?.isSecureScreenEnabled ?: true
    val announcePayer = settings?.announcePayerName ?: true
    val announceProvider = settings?.announceProviderName ?: true

    val powerManager = context.getSystemService(android.content.Context.POWER_SERVICE) as? PowerManager
    val isIgnoringBattery = powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: false

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SoundboxColors.Background)
            .padding(horizontal = SoundboxDimensions.ScreenPadding)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Header
        Text(
            text = "Settings & Security",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = SoundboxColors.PrimaryText
        )
        Text(
            text = "Device Configuration & System Preferences",
            style = MaterialTheme.typography.bodySmall,
            color = SoundboxColors.SecondaryText
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Notification Access Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SoundboxColors.Surface),
            border = BorderStroke(1.dp, SoundboxColors.Border),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isAccessGranted) SoundboxColors.PaymentSuccessContainer else SoundboxColors.WarningContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = if (isAccessGranted) SoundboxColors.PaymentSuccess else SoundboxColors.Warning,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Notification Listener Access",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SoundboxColors.PrimaryText
                        )
                        Text(
                            text = if (isAccessGranted) "Active & Listening" else "Permission Required",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isAccessGranted) SoundboxColors.PaymentSuccess else SoundboxColors.Warning
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SoundboxColors.PrimaryAccent)
                ) {
                    Text(
                        text = if (isAccessGranted) "Manage Android Listener Settings" else "Grant Notification Permission",
                        color = SoundboxColors.PrimaryAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Battery & Background Execution
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SoundboxColors.Surface),
            border = BorderStroke(1.dp, SoundboxColors.Border),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isIgnoringBattery) SoundboxColors.PaymentSuccessContainer else SoundboxColors.WarningContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BatteryChargingFull,
                            contentDescription = null,
                            tint = if (isIgnoringBattery) SoundboxColors.PaymentSuccess else SoundboxColors.Warning,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Unrestricted Battery Mode",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SoundboxColors.PrimaryText
                        )
                        Text(
                            text = if (isIgnoringBattery) "Optimized for continuous soundbox duty" else "Recommended: disable battery restrictions",
                            style = MaterialTheme.typography.bodySmall,
                            color = SoundboxColors.SecondaryText
                        )
                    }
                }

                if (!isIgnoringBattery) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                                data = Uri.parse("package:${context.packageName}")
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SoundboxColors.Warning)
                    ) {
                        Text("Disable Battery Restriction", color = SoundboxColors.Warning, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Speech & Announcement Preferences
        Text(
            text = "Announcement Customization",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = SoundboxColors.PrimaryText
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SoundboxColors.Surface),
            border = BorderStroke(1.dp, SoundboxColors.Border),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Announce Payer Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Announce Payer Name",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = SoundboxColors.PrimaryText
                        )
                        Text(
                            text = "Speak customer's name when available",
                            style = MaterialTheme.typography.bodySmall,
                            color = SoundboxColors.SecondaryText
                        )
                    }
                    Switch(
                        checked = announcePayer,
                        onCheckedChange = { scope.launch { container.settingsRepository.updateAnnouncePayer(it) } },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SoundboxColors.PrimaryAccent,
                            checkedTrackColor = SoundboxColors.PrimaryAccentContainer
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Announce Provider Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Announce UPI App Name",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = SoundboxColors.PrimaryText
                        )
                        Text(
                            text = "Speak app source (e.g. PhonePe / Google Pay)",
                            style = MaterialTheme.typography.bodySmall,
                            color = SoundboxColors.SecondaryText
                        )
                    }
                    Switch(
                        checked = announceProvider,
                        onCheckedChange = { scope.launch { container.settingsRepository.updateAnnounceProvider(it) } },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SoundboxColors.PrimaryAccent,
                            checkedTrackColor = SoundboxColors.PrimaryAccentContainer
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Screen Recording Guard Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Anti-Spyware Shield (FLAG_SECURE)",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = SoundboxColors.PrimaryText
                        )
                        Text(
                            text = "Blocks background screen recorders from seeing amounts",
                            style = MaterialTheme.typography.bodySmall,
                            color = SoundboxColors.SecondaryText
                        )
                    }
                    Switch(
                        checked = isSecureScreen,
                        onCheckedChange = { scope.launch { container.settingsRepository.updateSecureScreen(it) } },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SoundboxColors.PrimaryAccent,
                            checkedTrackColor = SoundboxColors.PrimaryAccentContainer
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Air-Gapped Zero-Trust Guarantee
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SoundboxColors.PaymentSuccessContainer.copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, SoundboxColors.PaymentSuccess.copy(alpha = 0.3f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SoundboxColors.PaymentSuccess),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "100% Air-Gapped & Offline",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SoundboxColors.PaymentSuccess
                    )
                    Text(
                        text = "Zero internet permissions. All payment data is processed locally.",
                        fontSize = 12.sp,
                        color = SoundboxColors.PrimaryText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
