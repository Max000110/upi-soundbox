package com.upisoundbox.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.upisoundbox.UpiSoundboxApp
import com.upisoundbox.core.model.Provider
import com.upisoundbox.ui.components.ProviderRow
import com.upisoundbox.ui.theme.DividerColor
import com.upisoundbox.ui.theme.TextPrimary
import com.upisoundbox.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun ProvidersScreen(modifier: Modifier = Modifier) {
    val container = UpiSoundboxApp.instance.container
    val settings by container.settingsRepository.settingsFlow.collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    val supportedProviders = listOf(
        Provider.PHONEPE,
        Provider.GOOGLE_PAY,
        Provider.PAYTM,
        Provider.BHIM,
        Provider.CRED,
        Provider.AMAZON_PAY,
        Provider.GENERIC
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Payment Apps",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Choose which UPI apps should trigger voice announcements.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))

        supportedProviders.forEach { provider ->
            val isEnabled = settings?.isProviderEnabled(provider) ?: (provider != Provider.GENERIC)
            ProviderRow(
                name = provider.displayName,
                isEnabled = isEnabled,
                onToggle = { checked ->
                    scope.launch {
                        container.settingsRepository.toggleProvider(provider.name, checked)
                    }
                }
            )
            HorizontalDivider(color = DividerColor)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
