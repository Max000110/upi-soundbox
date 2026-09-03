package com.upisoundbox.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = SoundboxColors.PrimaryAccent,
    onPrimary = Color.White,
    primaryContainer = SoundboxColors.PrimaryAccentContainer,
    onPrimaryContainer = SoundboxColors.PrimaryAccent,
    background = SoundboxColors.Background,
    onBackground = SoundboxColors.PrimaryText,
    surface = SoundboxColors.Surface,
    onSurface = SoundboxColors.PrimaryText,
    surfaceVariant = Color(0xFFF0F2F4),
    onSurfaceVariant = SoundboxColors.SecondaryText,
    outline = SoundboxColors.Border,
    error = SoundboxColors.Error
)

@Composable
fun UpiSoundboxTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = SoundboxColors.Background.toArgb()
            window.navigationBarColor = SoundboxColors.Surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        shapes = Shapes,
        content = content
    )
}
