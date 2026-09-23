package com.walknxt.app.presentation.home

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.walknxt.app.domain.model.SessionState
import com.walknxt.app.domain.model.WalkSession
import com.walknxt.app.platform.permission.CapabilityTracker
import com.walknxt.app.tracking.engine.TrackingCoordinator
import com.walknxt.app.tracking.service.WalkTrackingService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.walknxt.app.domain.repository.SessionRepository
import com.walknxt.app.data.local.entity.RoutePointEntity
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi

import com.walknxt.app.data.preferences.PreferencesManager

class HomeViewModel(
    private val context: Context,
    private val trackingCoordinator: TrackingCoordinator,
    private val capabilityTracker: CapabilityTracker,
    private val sessionRepository: SessionRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val dailyStepGoal: StateFlow<Int> = preferencesManager.dailyStepGoal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 10000)

    val sessionState: StateFlow<SessionState> = trackingCoordinator.currentState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SessionState.IDLE)

    val activeSession: StateFlow<WalkSession?> = trackingCoordinator.activeSession
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeRoutePoints: StateFlow<List<RoutePointEntity>> = trackingCoordinator.activeSession
        .filterNotNull()
        .flatMapLatest { session ->
            sessionRepository.observeRoutePointsForSession(session.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val capabilityState = capabilityTracker.capabilityState

    fun checkCapabilities() {
        capabilityTracker.updateCapabilityState()
    }

    private fun sendServiceCommand(actionType: String) {
        val intent = Intent(context, WalkTrackingService::class.java).apply {
            action = actionType
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && actionType == WalkTrackingService.ACTION_START) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun startWalk() {
        sendServiceCommand(WalkTrackingService.ACTION_START)
    }

    fun pauseWalk() {
        sendServiceCommand(WalkTrackingService.ACTION_PAUSE)
    }

    fun resumeWalk() {
        sendServiceCommand(WalkTrackingService.ACTION_RESUME)
    }

    fun dismissSummary() {
        viewModelScope.launch {
            trackingCoordinator.reset()
        }
    }

    fun finishWalk() {
        sendServiceCommand(WalkTrackingService.ACTION_STOP)
    }
}
