package com.upisoundbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.upisoundbox.security.SecurityManager
import com.upisoundbox.ui.navigation.Screen
import com.upisoundbox.ui.screens.DiagnosticsScreen
import com.upisoundbox.ui.screens.HistoryScreen
import com.upisoundbox.ui.screens.HomeScreen
import com.upisoundbox.ui.screens.SettingsScreen
import com.upisoundbox.ui.screens.VoiceScreen
import com.upisoundbox.ui.theme.SoundboxColors
import com.upisoundbox.ui.theme.UpiSoundboxTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by UpiSoundboxApp.instance.container.settingsRepository.settingsFlow.collectAsState(initial = null)

            LaunchedEffect(settings?.isSecureScreenEnabled) {
                // Apply FLAG_SECURE window protection based on user setting
                SecurityManager.applyWindowSecurity(
                    this@MainActivity,
                    settings?.isSecureScreenEnabled ?: true
                )
            }

            UpiSoundboxTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()

    val screens = listOf(
        Screen.Home,
        Screen.History,
        Screen.Voice,
        Screen.Settings,
        Screen.Diagnostics
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SoundboxColors.Background,
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            NavigationBar(
                containerColor = SoundboxColors.Surface
            ) {
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = {
                            Text(
                                text = screen.title,
                                maxLines = 1,
                                fontSize = 11.sp,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SoundboxColors.PrimaryAccent,
                            selectedTextColor = SoundboxColors.PrimaryAccent,
                            unselectedIconColor = SoundboxColors.SecondaryText,
                            unselectedTextColor = SoundboxColors.SecondaryText,
                            indicatorColor = SoundboxColors.PrimaryAccentContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.History.route) { HistoryScreen() }
            composable(Screen.Voice.route) { VoiceScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
            composable(Screen.Diagnostics.route) { DiagnosticsScreen() }
        }
    }
}
