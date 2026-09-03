package com.upisoundbox.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.upisoundbox.ui.theme.SoundboxColors

@Composable
fun SoundRipple(
    active: Boolean = false,
    size: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sound_ripple")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(750),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ripple_scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(750),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ripple_alpha"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        if (active) {
            // Outer expanding ripple ring
            Box(
                modifier = Modifier
                    .size(size)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale
                    )
                    .clip(CircleShape)
                    .background(SoundboxColors.PrimaryAccent.copy(alpha = alpha))
            )
        }

        // Inner solid badge with speaker icon
        Box(
            modifier = Modifier
                .size(size * 0.72f)
                .clip(CircleShape)
                .background(if (active) SoundboxColors.PaymentSuccessContainer else Color(0xFFF0F2F4)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Soundbox Speaker",
                tint = if (active) SoundboxColors.PaymentSuccess else SoundboxColors.PrimaryAccent,
                modifier = Modifier.size(size * 0.4f)
            )
        }
    }
}
