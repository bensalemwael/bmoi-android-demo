package com.bmoi.sifipdemo.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bmoi.sifipdemo.ui.login.CheckState
import com.bmoi.sifipdemo.ui.login.CheckStatus
import com.bmoi.sifipdemo.ui.theme.StatusError
import com.bmoi.sifipdemo.ui.theme.StatusOk

/** Single row inside the SIFIP authentication card. */
@Composable
fun CheckStepRow(
    check: CheckState,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusIndicator(check.status)
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = check.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = check.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                }
            }
            AnimatedVisibility(
                visible = check.resultMessage != null,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                val message = check.resultMessage.orEmpty()
                Text(
                    text = message,
                    modifier = Modifier.padding(start = 38.dp, top = 6.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (check.status) {
                        CheckStatus.Ok -> StatusOk
                        CheckStatus.Failed -> StatusError
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    },
                )
            }
        }
    }
}

@Composable
private fun StatusIndicator(status: CheckStatus) {
    val size = 24.dp
    when (status) {
        CheckStatus.Idle -> {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
            )
        }
        CheckStatus.Running -> {
            CircularProgressIndicator(
                modifier = Modifier.size(size),
                strokeWidth = 2.5.dp,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        CheckStatus.Ok -> {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(StatusOk),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "OK",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        CheckStatus.Failed -> {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(StatusError),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Échec",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(0.dp))
}
