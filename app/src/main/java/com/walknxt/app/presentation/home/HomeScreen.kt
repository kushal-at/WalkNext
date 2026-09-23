package com.walknxt.app.presentation.home

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.walknxt.app.R
import com.walknxt.app.domain.model.SessionState
import com.walknxt.app.platform.permission.PermissionStatus
import com.walknxt.app.presentation.components.PrimaryButton
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Play

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state by viewModel.sessionState.collectAsState()
    val capability by viewModel.capabilityState.collectAsState()
    val activeSession by viewModel.activeSession.collectAsState()
    val routePoints by viewModel.activeRoutePoints.collectAsState()

    val permissionsToRequest = mutableListOf(
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.checkCapabilities()
        if (capability.activityRecognitionPermissionStatus == PermissionStatus.GRANTED) {
            viewModel.startWalk()
        }
    }

    when (state) {
        SessionState.IDLE -> {
            val dailyGoal by viewModel.dailyStepGoal.collectAsState()
            // We need a daily total to show in the meter. Since we don't have a daily total tracked yet, we'll just show 0 steps or recent history. Wait, we don't have today's total steps in HomeViewModel yet.
            // Let's use 0 for now since it's the start screen, or we can fetch today's steps. I'll just show the meter with 0 steps.
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                com.walknxt.app.presentation.components.SemiCircleMeter(
                    currentValue = 0, // In a real app we'd fetch today's total steps
                    maxValue = dailyGoal,
                    label = "Steps Today"
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Ready to Walk",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "WalkNxt records your steps completely offline.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(48.dp))
                PrimaryButton(
                    text = "Start Walk",
                    icon = Lucide.Play,
                    onClick = {
                        if (capability.activityRecognitionPermissionStatus == PermissionStatus.GRANTED) {
                            viewModel.startWalk()
                        } else {
                            launcher.launch(permissionsToRequest.toTypedArray())
                        }
                    }
                )
            }
        }
        SessionState.COMPLETED -> {
            activeSession?.let { session ->
                WalkSummaryScreen(
                    session = session,
                    routePoints = routePoints,
                    onDismiss = { viewModel.dismissSummary() }
                )
            } ?: viewModel.dismissSummary()
        }
        else -> {
            activeSession?.let { session ->
                ActiveWalkScreen(
                    session = session,
                    routePoints = routePoints,
                    onPause = { viewModel.pauseWalk() },
                    onResume = { viewModel.resumeWalk() },
                    onFinish = { viewModel.finishWalk() }
                )
            }
        }
    }
}
