package com.upisoundbox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.upisoundbox.core.model.AnnouncementState
import com.upisoundbox.ui.theme.SoundboxColors

@Composable
fun AnnouncementStatus(
    state: AnnouncementState,
    modifier: Modifier = Modifier
) {
    val isSuccess = state == AnnouncementState.ANNOUNCED
    val isError = state == AnnouncementState.ANNOUNCEMENT_FAILED
    val isAnnouncing = state == AnnouncementState.ANNOUNCEMENT_STARTED || state == AnnouncementState.ANNOUNCEMENT_QUEUED

    val containerColor = when {
        isSuccess -> SoundboxColors.PaymentSuccessContainer
        isError -> SoundboxColors.ErrorContainer
        isAnnouncing -> SoundboxColors.PrimaryAccentContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = when {
        isSuccess -> SoundboxColors.PaymentSuccess
        isError -> SoundboxColors.Error
        isAnnouncing -> SoundboxColors.PrimaryAccent
        else -> SoundboxColors.SecondaryText
    }

    val icon = when {
        isSuccess -> Icons.Default.CheckCircle
        isError -> Icons.Default.ErrorOutline
        else -> Icons.Default.VolumeUp
    }

    val text = when (state) {
        AnnouncementState.ANNOUNCED -> "✓ Announced"
        AnnouncementState.ANNOUNCEMENT_STARTED -> "🔊 Announcing..."
        AnnouncementState.ANNOUNCEMENT_QUEUED -> "Queued"
        AnnouncementState.ANNOUNCEMENT_FAILED -> "Announcement failed"
        AnnouncementState.NEW -> "Received"
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor
            )
        }
    }
}
