package com.walknxt.app.tracking.engine

import com.walknxt.app.core.result.Result
import com.walknxt.app.core.result.TrackingError
import com.walknxt.app.domain.model.SessionState
import com.walknxt.app.domain.model.WalkSession
import kotlinx.coroutines.flow.StateFlow

interface TrackingEngine {
    val currentState: StateFlow<SessionState>
    val activeSession: StateFlow<WalkSession?>

    suspend fun start(): Result<Unit, TrackingError>
    suspend fun pause(): Result<Unit, TrackingError>
    suspend fun resume(): Result<Unit, TrackingError>
    suspend fun finish(): Result<Unit, TrackingError>
    suspend fun reset(): Result<Unit, TrackingError>
}
