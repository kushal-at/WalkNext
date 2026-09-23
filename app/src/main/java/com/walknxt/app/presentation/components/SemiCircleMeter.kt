package com.walknxt.app.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column

@Composable
fun SemiCircleMeter(
    currentValue: Int,
    maxValue: Int,
    label: String,
    modifier: Modifier = Modifier,
    unit: String = ""
) {
    val progress = if (maxValue > 0) (currentValue.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f) else 0f
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "ProgressAnimation"
    )

    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val indicatorColor = MaterialTheme.colorScheme.primary

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2f) // 2:1 aspect ratio for a semi-circle
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().aspectRatio(2f)) {
            val strokeWidth = 24.dp.toPx()
            
            // Draw background track
            drawArc(
                color = trackColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Draw progress indicator
            drawArc(
                color = indicatorColor,
                startAngle = 180f,
                sweepAngle = 180f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 8.dp)) {
            Text(
                text = "$currentValue",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$label / $maxValue $unit".trim(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
