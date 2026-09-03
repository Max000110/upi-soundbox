package com.upisoundbox.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class SoundboxThemeId(
    val displayName: String,
    val primaryColor: Color,
    val isDark: Boolean = true
) {
    DARK_NAVY("Dark Navy (Default)", Color(0xFF38BDF8), isDark = true),
    NEON_PURPLE("Neon Purple", Color(0xFF8B5CF6), isDark = true),
    OCEAN_BLUE("Ocean Blue", Color(0xFF0284C7), isDark = false),
    FOREST_GREEN("Forest Green", Color(0xFF10B981), isDark = true),
    SUNSET_ORANGE("Sunset Orange", Color(0xFFF97316), isDark = true),
    ROYAL_INDIGO("Royal Indigo", Color(0xFF6366F1), isDark = true),
    AMOLED_DARK("AMOLED Dark", Color(0xFF38BDF8), isDark = true),
    TEAL_CYAN("Teal Cyan", Color(0xFF06B6D4), isDark = true),
    SAND_BEIGE("Sand Beige", Color(0xFFF59E0B), isDark = false),
    ROSE_PINK("Rose Pink", Color(0xFFEC4899), isDark = true),
    CYBER_GRID("Cyber Grid", Color(0xFF06B6D4), isDark = true);

    fun getColorScheme(): ColorScheme {
        return when (this) {
            DARK_NAVY, AMOLED_DARK -> darkColorScheme(
                primary = Color(0xFF38BDF8),
                onPrimary = Color.Black,
                primaryContainer = Color(0xFF1E293B),
                onPrimaryContainer = Color(0xFFBAE6FD),
                background = Color(0xFF0B0F19),
                onBackground = Color(0xFFFFFFFF),
                surface = Color(0xFF162032),
                onSurface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFF1E293B),
                onSurfaceVariant = Color(0xFF94A3B8),
                outline = Color(0xFF26354D)
            )
            NEON_PURPLE -> darkColorScheme(
                primary = Color(0xFFA78BFA),
                onPrimary = Color.Black,
                primaryContainer = Color(0xFF2E1065),
                onPrimaryContainer = Color(0xFFDDD6FE),
                background = Color(0xFF0F0A1C),
                onBackground = Color(0xFFFFFFFF),
                surface = Color(0xFF1A122E),
                onSurface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFF2B1D4B),
                onSurfaceVariant = Color(0xFFC4B5FD),
                outline = Color(0xFF3E2B68)
            )
            FOREST_GREEN -> darkColorScheme(
                primary = Color(0xFF34D399),
                onPrimary = Color.Black,
                primaryContainer = Color(0xFF064E3B),
                onPrimaryContainer = Color(0xFFA7F3D0),
                background = Color(0xFF061A12),
                onBackground = Color(0xFFFFFFFF),
                surface = Color(0xFF0D2E21),
                onSurface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFF134532),
                onSurfaceVariant = Color(0xFFA7F3D0),
                outline = Color(0xFF1F5C44)
            )
            SUNSET_ORANGE -> darkColorScheme(
                primary = Color(0xFFFB923C),
                onPrimary = Color.Black,
                primaryContainer = Color(0xFF7C2D12),
                onPrimaryContainer = Color(0xFFFFEDD5),
                background = Color(0xFF180D06),
                onBackground = Color(0xFFFFFFFF),
                surface = Color(0xFF2A170C),
                onSurface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFF3E2213),
                onSurfaceVariant = Color(0xFFFDBA74),
                outline = Color(0xFF5A311C)
            )
            ROYAL_INDIGO -> darkColorScheme(
                primary = Color(0xFF818CF8),
                onPrimary = Color.Black,
                primaryContainer = Color(0xFF312E81),
                onPrimaryContainer = Color(0xFFE0E7FF),
                background = Color(0xFF0C0E1E),
                onBackground = Color(0xFFFFFFFF),
                surface = Color(0xFF151934),
                onSurface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFF20264F),
                onSurfaceVariant = Color(0xFFA5B4FC),
                outline = Color(0xFF323B75)
            )
            TEAL_CYAN -> darkColorScheme(
                primary = Color(0xFF2DD4BF),
                onPrimary = Color.Black,
                primaryContainer = Color(0xFF134E4A),
                onPrimaryContainer = Color(0xFFCCFBF1),
                background = Color(0xFF061817),
                onBackground = Color(0xFFFFFFFF),
                surface = Color(0xFF0C2B29),
                onSurface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFF14403D),
                onSurfaceVariant = Color(0xFF99F6E4),
                outline = Color(0xFF1D5A56)
            )
            ROSE_PINK -> darkColorScheme(
                primary = Color(0xFFF472B6),
                onPrimary = Color.Black,
                primaryContainer = Color(0xFF831843),
                onPrimaryContainer = Color(0xFFFCE7F3),
                background = Color(0xFF180611),
                onBackground = Color(0xFFFFFFFF),
                surface = Color(0xFF2B0E20),
                onSurface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFF3E162F),
                onSurfaceVariant = Color(0xFFF9A8D4),
                outline = Color(0xFF5C2346)
            )
            CYBER_GRID -> darkColorScheme(
                primary = Color(0xFF06B6D4),
                onPrimary = Color.Black,
                primaryContainer = Color(0xFF164E63),
                onPrimaryContainer = Color(0xFFA5F3FC),
                background = Color(0xFF05131D),
                onBackground = Color(0xFFFFFFFF),
                surface = Color(0xFF0A2233),
                onSurface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFF113854),
                onSurfaceVariant = Color(0xFF67E8F9),
                outline = Color(0xFF155E75)
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
        }
    }

    companion object {
        fun fromName(name: String?): SoundboxThemeId {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: DARK_NAVY
        }
    }
}
