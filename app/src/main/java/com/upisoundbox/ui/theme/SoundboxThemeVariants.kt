package com.upisoundbox.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class SoundboxThemeId(
    val displayName: String,
    val primaryColor: Color,
    val isDark: Boolean = false
) {
    NEON_PURPLE("Neon Purple", Color(0xFF8B5CF6), isDark = true),
    OCEAN_BLUE("Ocean Blue", Color(0xFF0284C7), isDark = false),
    FOREST_GREEN("Forest Green", Color(0xFF168A55), isDark = false),
    SUNSET_ORANGE("Sunset Orange", Color(0xFFF97316), isDark = false),
    ROYAL_INDIGO("Royal Indigo", Color(0xFF4F46E5), isDark = false),
    AMOLED_DARK("AMOLED Dark", Color(0xFF38BDF8), isDark = true),
    TEAL_CYAN("Teal Cyan", Color(0xFF0D9488), isDark = false),
    SAND_BEIGE("Sand Beige", Color(0xFFD97706), isDark = false),
    ROSE_PINK("Rose Pink", Color(0xFFEC4899), isDark = false),
    CYBER_GRID("Cyber Grid", Color(0xFF06B6D4), isDark = true);

    fun getColorScheme(): ColorScheme {
        return when (this) {
            NEON_PURPLE -> darkColorScheme(
                primary = Color(0xFFA78BFA),
                onPrimary = Color.Black,
                primaryContainer = Color(0xFF2E1065),
                onPrimaryContainer = Color(0xFFDDD6FE),
                background = Color(0xFF0F0A1C),
                onBackground = Color(0xFFF5F3FF),
                surface = Color(0xFF18102E),
                onSurface = Color(0xFFF5F3FF),
                surfaceVariant = Color(0xFF251A45),
                onSurfaceVariant = Color(0xFFC4B5FD),
                outline = Color(0xFF3B2D6B)
            )
            OCEAN_BLUE -> lightColorScheme(
                primary = Color(0xFF0284C7),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFE0F2FE),
                onPrimaryContainer = Color(0xFF0369A1),
                background = Color(0xFFF0F9FF),
                onBackground = Color(0xFF0C4A6E),
                surface = Color(0xFFFFFFFF),
                onSurface = Color(0xFF0C4A6E),
                surfaceVariant = Color(0xFFE0F2FE),
                onSurfaceVariant = Color(0xFF0284C7),
                outline = Color(0xFFBAE6FD)
            )
            FOREST_GREEN -> lightColorScheme(
                primary = Color(0xFF168A55),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFE9F7F0),
                onPrimaryContainer = Color(0xFF168A55),
                background = Color(0xFFF7FBF9),
                onBackground = Color(0xFF16181D),
                surface = Color(0xFFFFFFFF),
                onSurface = Color(0xFF16181D),
                surfaceVariant = Color(0xFFE9F7F0),
                onSurfaceVariant = Color(0xFF168A55),
                outline = Color(0xFFD1E7DD)
            )
            SUNSET_ORANGE -> lightColorScheme(
                primary = Color(0xFFEA580C),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFFFEDD5),
                onPrimaryContainer = Color(0xFF9A3412),
                background = Color(0xFFFFF7ED),
                onBackground = Color(0xFF431407),
                surface = Color(0xFFFFFFFF),
                onSurface = Color(0xFF431407),
                surfaceVariant = Color(0xFFFFEDD5),
                onSurfaceVariant = Color(0xFFC2410C),
                outline = Color(0xFFFED7AA)
            )
            ROYAL_INDIGO -> lightColorScheme(
                primary = Color(0xFF4F46E5),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFEEF2FF),
                onPrimaryContainer = Color(0xFF3730A3),
                background = Color(0xFFF8FAFC),
                onBackground = Color(0xFF1E1B4B),
                surface = Color(0xFFFFFFFF),
                onSurface = Color(0xFF1E1B4B),
                surfaceVariant = Color(0xFFEEF2FF),
                onSurfaceVariant = Color(0xFF4F46E5),
                outline = Color(0xFFC7D2FE)
            )
            AMOLED_DARK -> darkColorScheme(
                primary = Color(0xFF38BDF8),
                onPrimary = Color.Black,
                primaryContainer = Color(0xFF1E293B),
                onPrimaryContainer = Color(0xFFBAE6FD),
                background = Color(0xFF000000),
                onBackground = Color(0xFFF8FAFC),
                surface = Color(0xFF111827),
                onSurface = Color(0xFFF8FAFC),
                surfaceVariant = Color(0xFF1F2937),
                onSurfaceVariant = Color(0xFF94A3B8),
                outline = Color(0xFF374151)
            )
            TEAL_CYAN -> lightColorScheme(
                primary = Color(0xFF0D9488),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFCCFBF1),
                onPrimaryContainer = Color(0xFF115E59),
                background = Color(0xFFF0FDFA),
                onBackground = Color(0xFF134E4A),
                surface = Color(0xFFFFFFFF),
                onSurface = Color(0xFF134E4A),
                surfaceVariant = Color(0xFFCCFBF1),
                onSurfaceVariant = Color(0xFF0F766E),
                outline = Color(0xFF99F6E4)
            )
            SAND_BEIGE -> lightColorScheme(
                primary = Color(0xFFD97706),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFFEF3C7),
                onPrimaryContainer = Color(0xFF92400E),
                background = Color(0xFFFFFBEB),
                onBackground = Color(0xFF451A03),
                surface = Color(0xFFFFFFFF),
                onSurface = Color(0xFF451A03),
                surfaceVariant = Color(0xFFFEF3C7),
                onSurfaceVariant = Color(0xFFB45309),
                outline = Color(0xFFFDE68A)
            )
            ROSE_PINK -> lightColorScheme(
                primary = Color(0xFFDB2777),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFFCE7F3),
                onPrimaryContainer = Color(0xFF831843),
                background = Color(0xFFFDF2F8),
                onBackground = Color(0xFF500724),
                surface = Color(0xFFFFFFFF),
                onSurface = Color(0xFF500724),
                surfaceVariant = Color(0xFFFCE7F3),
                onSurfaceVariant = Color(0xFFBE185D),
                outline = Color(0xFFFBCFE8)
            )
            CYBER_GRID -> darkColorScheme(
                primary = Color(0xFF06B6D4),
                onPrimary = Color.Black,
                primaryContainer = Color(0xFF164E63),
                onPrimaryContainer = Color(0xFFA5F3FC),
                background = Color(0xFF05131D),
                onBackground = Color(0xFFECFEFF),
                surface = Color(0xFF0A2233),
                onSurface = Color(0xFFECFEFF),
                surfaceVariant = Color(0xFF113854),
                onSurfaceVariant = Color(0xFF67E8F9),
                outline = Color(0xFF155E75)
            )
        }
    }

    companion object {
        fun fromName(name: String?): SoundboxThemeId {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: OCEAN_BLUE
        }
    }
}
