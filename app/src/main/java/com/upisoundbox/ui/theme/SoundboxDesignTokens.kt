package com.upisoundbox.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object SoundboxDimensions {
    val ScreenPadding = 18.dp
    val CardPadding = 20.dp

    val SmallSpacing = 8.dp
    val MediumSpacing = 12.dp
    val LargeSpacing = 20.dp
    val SectionSpacing = 24.dp

    val SmallRadius = 8.dp
    val MediumRadius = 12.dp
    val LargeRadius = 16.dp
    val HeroRadius = 24.dp
}

object SoundboxTypography {
    val PaymentAmount = TextStyle(
        fontSize = 52.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-1.5).sp
    )

    val ScreenTitle = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )

    val SectionTitle = TextStyle(
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold
    )

    val Body = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal
    )

    val Caption = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal
    )
}

object SoundboxColors {
    val Background = Color(0xFFF7F8FA)
    val Surface = Color(0xFFFFFFFF)
    val PrimaryText = Color(0xFF16181D)
    val SecondaryText = Color(0xFF6B7078)
    val Border = Color(0xFFE4E7EB)

    val PaymentSuccess = Color(0xFF168A55)
    val PaymentSuccessContainer = Color(0xFFE9F7F0)

    val PrimaryAccent = Color(0xFF2563EB)
    val PrimaryAccentContainer = Color(0xFFEFF6FF)

    val Warning = Color(0xFFC98200)
    val WarningContainer = Color(0xFFFFF4DC)

    val Error = Color(0xFFD64545)
    val ErrorContainer = Color(0xFFFEE2E2)
}

val SoundboxLightColorScheme = lightColorScheme(
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
