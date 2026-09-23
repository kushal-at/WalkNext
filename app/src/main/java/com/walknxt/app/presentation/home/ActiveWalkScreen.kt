package com.walknxt.app.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import com.walknxt.app.data.local.entity.RoutePointEntity
import com.walknxt.app.domain.model.SessionState
import com.walknxt.app.domain.model.WalkSession
import com.walknxt.app.presentation.components.ConfidenceIndicator
import com.walknxt.app.presentation.components.MetricCard
import com.walknxt.app.presentation.components.PrimaryButton
import com.walknxt.app.presentation.components.DestructiveButton
import com.walknxt.app.presentation.components.StatusBanner
import com.walknxt.app.presentation.util.Formatter
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pause
import com.composables.icons.lucide.Play
import com.composables.icons.lucide.Square

@Composable
fun ActiveWalkScreen(
    session: WalkSession,
    routePoints: List<RoutePointEntity>,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (session.state == SessionState.PAUSED) {
            StatusBanner(
                message = "Walk Paused",
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                textColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        com.walknxt.app.presentation.components.SemiCircleMeter(
            currentValue = session.stepCount,
            maxValue = 10000, // We could pass daily goal here, but standard goal for a session meter is fine for now, or maybe just a dynamic max value. Let's make it 10000 for now.
            label = "Steps"
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        MetricCard(
            label = "Distance",
            value = Formatter.formatDistance(session.distance),
            unit = Formatter.distanceUnit(),
            isProminent = true
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
                label = "Duration",
                value = Formatter.formatDuration(session.activeDuration),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        if (session.state == SessionState.WALKING) {
            PrimaryButton(text = "Pause Walk", icon = Lucide.Pause, onClick = onPause)
        } else {
            Row(modifier = Modifier.fillMaxWidth()) {
                PrimaryButton(
                    text = "Resume",
                    icon = Lucide.Play,
                    onClick = onResume,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                DestructiveButton(
                    text = "Finish",
                    icon = Lucide.Square,
                    onClick = onFinish,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
