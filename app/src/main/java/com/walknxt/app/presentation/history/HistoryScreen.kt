package com.walknxt.app.presentation.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.walknxt.app.domain.model.SessionSummary
import com.walknxt.app.presentation.components.ConfidenceIndicator
import com.walknxt.app.presentation.components.EmptyState
import com.walknxt.app.presentation.util.Formatter
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val history by viewModel.history.collectAsState()

    if (history.isEmpty()) {
        EmptyState(
            title = "No Walks Yet",
            description = "Start your first walk from the Home tab. Your history will appear here completely offline."
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            items(history, key = { it.id }) { session ->
                HistoryItem(session = session) {
                    // Navigate to detail (not implemented yet)
                }
            }
        }
    }
}

@Composable
fun HistoryItem(session: SessionSummary, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy • h:mm a", Locale.getDefault())
            val dateStr = dateFormat.format(Date(session.startTime))
            
            Text(
                text = dateStr,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        text = Formatter.formatDistance(session.distance) + " " + Formatter.distanceUnit(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Distance",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Text(
                        text = "${session.caloriesBurned}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "kcal",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                    Text(
                        text = session.steps.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Steps",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            ConfidenceIndicator(confidence = session.confidence)
        }
    }
}
