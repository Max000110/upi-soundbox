package com.upisoundbox.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object History : Screen("history", "History", Icons.Default.History)
    data object LivePayment : Screen("live_payment", "Live", Icons.Default.Mic)
    data object Voice : Screen("voice", "Sound", Icons.Default.VolumeUp)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    data object Diagnostics : Screen("diagnostics", "Diagnostics", Icons.Default.Analytics)
}
