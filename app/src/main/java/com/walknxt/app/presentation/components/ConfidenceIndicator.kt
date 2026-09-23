package com.walknxt.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.walknxt.app.domain.model.ConfidenceLevel
import com.walknxt.app.presentation.theme.ConfidenceHigh
import com.walknxt.app.presentation.theme.ConfidenceLow
import com.walknxt.app.presentation.theme.ConfidenceMedium

@Composable
fun ConfidenceIndicator(
    confidence: ConfidenceLevel,
    modifier: Modifier = Modifier
) {
    val (color, label) = when (confidence) {
        ConfidenceLevel.HIGH -> ConfidenceHigh to "High Confidence"
        ConfidenceLevel.MEDIUM -> ConfidenceMedium to "Moderate Confidence"
        ConfidenceLevel.LOW -> ConfidenceLow to "Low Confidence"
        ConfidenceLevel.UNAVAILABLE -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) to "Uncertain"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
