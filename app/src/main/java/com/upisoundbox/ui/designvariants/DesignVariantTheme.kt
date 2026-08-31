package com.upisoundbox.ui.designvariants

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class VariantPalette(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val border: Color,
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val success: Color,
    val cardRadius: Dp,
    val buttonRadius: Dp
)

object DesignVariantTheme {

    fun getPalette(variant: DesignVariant): VariantPalette {
        return when (variant) {
            DesignVariant.DESIGN_01 -> VariantPalette(
                background = Color(0xFFF8F9FA),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFE8F5E9),
                border = Color(0xFFE0E0E0),
                primary = Color(0xFF004D40),
                onPrimary = Color(0xFFFFFFFF),
                secondary = Color(0xFF00796B),
                textPrimary = Color(0xFF212121),
                textSecondary = Color(0xFF757575),
                success = Color(0xFF2E7D32),
                cardRadius = 14.dp,
                buttonRadius = 10.dp
            )
            DesignVariant.DESIGN_02 -> VariantPalette(
                background = Color(0xFFF4F1EA),
                surface = Color(0xFFFAF9F6),
                surfaceVariant = Color(0xFFEBE6DC),
                border = Color(0xFFD3CCC0),
                primary = Color(0xFF1C2A1E),
                onPrimary = Color(0xFFFFFFFF),
                secondary = Color(0xFF384D3B),
                textPrimary = Color(0xFF1A1A1A),
                textSecondary = Color(0xFF6B6860),
                success = Color(0xFF235336),
                cardRadius = 6.dp,
                buttonRadius = 4.dp
            )
            DesignVariant.DESIGN_03 -> VariantPalette(
                background = Color(0xFFFBF9F5),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFF3EFEA),
                border = Color(0xFFE8E2D9),
                primary = Color(0xFF141414),
                onPrimary = Color(0xFFFFFFFF),
                secondary = Color(0xFF8C6D46),
                textPrimary = Color(0xFF141414),
                textSecondary = Color(0xFF7A756D),
                success = Color(0xFF2E6930),
                cardRadius = 4.dp,
                buttonRadius = 4.dp
            )
            DesignVariant.DESIGN_04 -> VariantPalette(
                background = Color(0xFFF0F2F5),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFE2E8F0),
                border = Color(0xFFCBD5E1),
                primary = Color(0xFF0F2537),
                onPrimary = Color(0xFFFFFFFF),
                secondary = Color(0xFF0284C7),
                textPrimary = Color(0xFF0F172A),
                textSecondary = Color(0xFF64748B),
                success = Color(0xFF059669),
                cardRadius = 8.dp,
                buttonRadius = 6.dp
            )
            DesignVariant.DESIGN_05 -> VariantPalette(
                background = Color(0xFFEDF2F7),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFE0E7FF),
                border = Color(0xFFC7D2FE),
                primary = Color(0xFF1A56DB),
                onPrimary = Color(0xFFFFFFFF),
                secondary = Color(0xFF4338CA),
                textPrimary = Color(0xFF1E293B),
                textSecondary = Color(0xFF64748B),
                success = Color(0xFF10B981),
                cardRadius = 24.dp,
                buttonRadius = 20.dp
            )
            DesignVariant.DESIGN_06 -> VariantPalette(
                background = Color(0xFFF5F5F0),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFEDECE4),
                border = Color(0xFFDDDCD3),
                primary = Color(0xFF2E5A44),
                onPrimary = Color(0xFFFFFFFF),
                secondary = Color(0xFF4E7C64),
                textPrimary = Color(0xFF242C27),
                textSecondary = Color(0xFF6B756F),
                success = Color(0xFF2E5A44),
                cardRadius = 18.dp,
                buttonRadius = 14.dp
            )
            DesignVariant.DESIGN_07 -> VariantPalette(
                background = Color(0xFFFFFFFF),
                surface = Color(0xFFF9FAFB),
                surfaceVariant = Color(0xFFF3F4F6),
                border = Color(0xFFE5E7EB),
                primary = Color(0xFF111827),
                onPrimary = Color(0xFFFFFFFF),
                secondary = Color(0xFF374151),
                textPrimary = Color(0xFF111827),
                textSecondary = Color(0xFF6B7280),
                success = Color(0xFF10B981),
                cardRadius = 4.dp,
                buttonRadius = 4.dp
            )
            DesignVariant.DESIGN_08 -> VariantPalette(
                background = Color(0xFFFFFBEB),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFFEF3C7),
                border = Color(0xFFFDE68A),
                primary = Color(0xFFB45309),
                onPrimary = Color(0xFFFFFFFF),
                secondary = Color(0xFF92400E),
                textPrimary = Color(0xFF18181B),
                textSecondary = Color(0xFF71717A),
                success = Color(0xFF15803D),
                cardRadius = 16.dp,
                buttonRadius = 12.dp
            )
            DesignVariant.DESIGN_09 -> VariantPalette(
                background = Color(0xFFEAEEF2),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFDDE3EA),
                border = Color(0xFFCBD3DC),
                primary = Color(0xFF0B1B2B),
                onPrimary = Color(0xFFFFFFFF),
                secondary = Color(0xFF2563EB),
                textPrimary = Color(0xFF0D1B2A),
                textSecondary = Color(0xFF5A6E85),
                success = Color(0xFF16A34A),
                cardRadius = 4.dp,
                buttonRadius = 4.dp
            )
            DesignVariant.DESIGN_10 -> VariantPalette(
                background = Color(0xFFF0F4F8),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFE2E8F0),
                border = Color(0xFFCBD5E1),
                primary = Color(0xFF0B1320),
                onPrimary = Color(0xFFFFFFFF),
                secondary = Color(0xFF6366F1),
                textPrimary = Color(0xFF0F172A),
                textSecondary = Color(0xFF64748B),
                success = Color(0xFF059669),
                cardRadius = 20.dp,
                buttonRadius = 16.dp
            )
        }
    }
}
