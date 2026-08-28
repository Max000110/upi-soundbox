package com.upisoundbox.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AccentEvergreen,
    onPrimary = SurfacePrimary,
    primaryContainer = AccentLight,
    onPrimaryContainer = AccentEvergreen,
    secondary = AccentEvergreen,
    onSecondary = SurfacePrimary,
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
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
