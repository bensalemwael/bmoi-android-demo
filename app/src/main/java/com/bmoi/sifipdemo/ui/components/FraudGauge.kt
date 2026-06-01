package com.bmoi.sifipdemo.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bmoi.sifipdemo.ui.theme.StatusError
import com.bmoi.sifipdemo.ui.theme.StatusOk
import com.bmoi.sifipdemo.ui.theme.StatusWarn

/**
 * Semi-circular gauge that animates the fraud score (0–100).
 *  - 0–34   → green
 *  - 35–69  → orange
 *  - 70–100 → red
 */
@Composable
fun FraudGauge(
    score: Int,
    modifier: Modifier = Modifier,
) {
    val animated = remember { Animatable(0f) }
    LaunchedEffect(score) {
        animated.animateTo(
            targetValue = score.coerceIn(0, 100).toFloat(),
            animationSpec = tween(durationMillis = 1100),
        )
    }
    val color = when {
        animated.value < 35f -> StatusOk
        animated.value < 70f -> StatusWarn
        else -> StatusError
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(260.dp, 180.dp)) {
            val strokeWidth = 22.dp.toPx()
            val arcSize = Size(size.width - strokeWidth, size.height * 2 - strokeWidth)
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

            // background track
            drawArc(
                color = Color(0xFFE3E8F2),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )

            // active arc
            val sweep = 180f * (animated.value / 100f)
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(StatusOk, StatusWarn, StatusError),
                ),
                startAngle = 180f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 28.dp),
        ) {
            Text(
                text = "${animated.value.toInt()}%",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = color,
            )
            Text(
                text = "Score de fraude IA",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
fun FraudReasons(reasons: List<String>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        reasons.forEach { reason ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .padding(end = 0.dp),
                )
                Text(
                    text = "• $reason",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                )
            }
        }
    }
}
