package com.walknxt.app.presentation.stats

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.walknxt.app.domain.model.Distance
import com.walknxt.app.presentation.components.EmptyState
import com.walknxt.app.presentation.components.MetricCard
import com.walknxt.app.presentation.util.Formatter

@Composable
fun StatsScreen(viewModel: StatsViewModel) {
    val allTime by viewModel.allTimeAnalytics.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Lifetime Statistics",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        val data = allTime
        if (data == null || data.sessionCount == 0) {
            EmptyState(
                title = "No Data Yet",
                description = "Your lifetime walking statistics will appear here."
            )
        } else {
            MetricCard(
                label = "Total Distance",
                value = Formatter.formatDistance(Distance(data.totalDistanceMeters)),
                unit = Formatter.distanceUnit(),
                isProminent = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                MetricCard(
                    label = "Total Steps",
                    value = data.totalSteps.toString(),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                MetricCard(
                    label = "Sessions",
                    value = data.sessionCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
