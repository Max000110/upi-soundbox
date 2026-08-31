package com.upisoundbox.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryM3,
    onPrimary = SurfacePrimary,
    primaryContainer = PrimaryContainerM3,
    onPrimaryContainer = OnPrimaryContainerM3,
    secondary = SecondaryM3,
    onSecondary = SurfacePrimary,
    background = BackgroundM3,
    onBackground = TextPrimary,
    surface = SurfacePrimary,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceSecondary,
    onSurfaceVariant = TextSecondary,
    outline = BorderColor,
    outlineVariant = DividerColor,
    error = SemanticError,
    onError = SurfacePrimary,
    errorContainer = SemanticErrorContainer,
    onErrorContainer = SemanticError
)

@Composable
fun UpiSoundboxTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BackgroundM3.toArgb()
            window.navigationBarColor = SurfacePrimary.toArgb()
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
