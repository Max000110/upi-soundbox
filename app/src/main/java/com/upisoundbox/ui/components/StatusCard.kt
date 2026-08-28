package com.upisoundbox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.upisoundbox.ui.theme.AccentEvergreen
import com.upisoundbox.ui.theme.BorderColor
import com.upisoundbox.ui.theme.SemanticError
import com.upisoundbox.ui.theme.SemanticErrorContainer
import com.upisoundbox.ui.theme.SemanticSuccess
import com.upisoundbox.ui.theme.SemanticSuccessContainer
import com.upisoundbox.ui.theme.SemanticWarning
import com.upisoundbox.ui.theme.SemanticWarningContainer
import com.upisoundbox.ui.theme.TextPrimary
import com.upisoundbox.ui.theme.TextSecondary

enum class StatusLevel {
    READY,
    ATTENTION,
    BLOCKED
}

@Composable
fun StatusCard(
    level: StatusLevel,
    title: String,
    subtitle: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val (bgColor, iconColor, icon) = when (level) {
        StatusLevel.READY -> Triple(SemanticSuccessContainer, SemanticSuccess, Icons.Default.CheckCircle)
        StatusLevel.ATTENTION -> Triple(SemanticWarningContainer, SemanticWarning, Icons.Default.Warning)
        StatusLevel.BLOCKED -> Triple(SemanticErrorContainer, SemanticError, Icons.Default.Error)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(bgColor)
            .border(1.dp, BorderColor, MaterialTheme.shapes.large)
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(iconColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            if (actionText != null && onActionClick != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onActionClick,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentEvergreen),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = actionText)
                }
            }
        }
    }
}
