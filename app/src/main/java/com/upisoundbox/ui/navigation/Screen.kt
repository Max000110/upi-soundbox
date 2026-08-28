package com.upisoundbox.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object Providers : Screen("providers", "Providers", Icons.Default.Apps)
    data object Voice : Screen("voice", "Voice", Icons.Default.RecordVoiceOver)
    data object History : Screen("history", "History", Icons.Default.History)
    data object Diagnostics : Screen("diagnostics", "Diagnostics", Icons.Default.Analytics)
}
