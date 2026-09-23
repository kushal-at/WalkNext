package com.walknxt.app.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import com.walknxt.app.data.local.entity.RoutePointEntity
import com.walknxt.app.domain.model.WalkSession
import com.walknxt.app.presentation.components.MetricCard
import com.walknxt.app.presentation.components.PrimaryButton
import com.walknxt.app.presentation.util.Formatter

import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Check

@Composable
fun WalkSummaryScreen(
    session: WalkSession,
    routePoints: List<RoutePointEntity>,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Walk Completed!",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        Spacer(modifier = Modifier.height(16.dp))
        
        MetricCard(
            label = "Total Distance",
            value = Formatter.formatDistance(session.distance),
            unit = Formatter.distanceUnit(),
            isProminent = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        MetricCard(
            label = "Total Steps",
            value = session.stepCount.toString()
        )
        Spacer(modifier = Modifier.height(16.dp))
        MetricCard(
            label = "Total Duration",
            value = Formatter.formatDuration(session.activeDuration)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            MetricCard(
                label = "Calories",
                value = "${session.caloriesBurned}",
                unit = "kcal",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            MetricCard(
                label = "Avg Pace",
                value = String.format("%.2f", session.averagePaceMinPerKm),
                unit = "min/km",
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        PrimaryButton(text = "Done", icon = Lucide.Check, onClick = onDismiss)
        Spacer(modifier = Modifier.height(32.dp))
    }
}
