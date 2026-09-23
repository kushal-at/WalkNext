package com.walknxt.app.tracking.engine

import com.walknxt.app.core.result.Result
import com.walknxt.app.core.result.TrackingError
import com.walknxt.app.domain.measurement.MeasurementEngine
import com.walknxt.app.domain.measurement.MeasurementEngineImpl
import com.walknxt.app.domain.model.*
import com.walknxt.app.domain.repository.SessionRepository
import com.walknxt.app.platform.clock.Clock
import com.walknxt.app.platform.location.LocationDataSource
import com.walknxt.app.platform.permission.CapabilityTracker
import com.walknxt.app.platform.permission.PermissionStatus
import com.walknxt.app.platform.sensor.MotionDataSource
import com.walknxt.app.platform.sensor.StepDataSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.UUID

import java.util.concurrent.atomic.AtomicBoolean

class TrackingCoordinator(
    private val clock: Clock,
    private val stepDataSource: StepDataSource,
    private val motionDataSource: MotionDataSource,
    private val locationDataSource: LocationDataSource,
    private val capabilityTracker: CapabilityTracker,
    private val sessionRepository: SessionRepository,
    private val scope: CoroutineScope
) : TrackingEngine {

    private val _currentState = MutableStateFlow(SessionState.IDLE)
    override val currentState: StateFlow<SessionState> = _currentState.asStateFlow()

    private val _activeSession = MutableStateFlow<WalkSession?>(null)
    override val activeSession: StateFlow<WalkSession?> = _activeSession.asStateFlow()

    private val measurementEngine: MeasurementEngine = MeasurementEngineImpl()
    private var trackingJob: Job? = null
    private var currentSessionId: String? = null
    private var lastPauseTimestamp: Long = 0L

    private val pendingRoutePoints = mutableListOf<com.walknxt.app.data.local.entity.RoutePointEntity>()
    private var previousLocation: LocationObservation? = null

    private val isTransitioning = AtomicBoolean(false)

    override suspend fun start(): Result<Unit, TrackingError> {
        if (!isTransitioning.compareAndSet(false, true)) return Result.Error(TrackingError.InvalidState)
        try {
            if (_currentState.value != SessionState.IDLE && _currentState.value != SessionState.COMPLETED) {
                return Result.Error(TrackingError.InvalidState)
            }

            capabilityTracker.updateCapabilityState()
            val cap = capabilityTracker.capabilityState.value
            
            if (cap.activityRecognitionPermissionStatus != PermissionStatus.GRANTED) {
                return Result.Error(TrackingError.PermissionRequired)
            }
            
            if (!cap.isStepSensorAvailable && !cap.isLocationAvailable) {
                return Result.Error(TrackingError.SensorUnavailable)
            }

            _currentState.value = SessionState.STARTING
            measurementEngine.reset()
            pendingRoutePoints.clear()
            previousLocation = null

            val sessionId = UUID.randomUUID().toString()
            currentSessionId = sessionId
            val startTime = clock.currentTimeMillis()

            val initialSession = WalkSession(
                id = sessionId,
                startTime = startTime,
                endTime = null,
                activeDuration = Duration(0),
                pausedDuration = Duration(0),
                totalDuration = Duration(0),
                stepCount = 0,
                distance = Distance(0.0),
                measurementMode = MeasurementMode.UNAVAILABLE,
                averageSpeed = null,
                confidence = ConfidenceLevel.UNAVAILABLE,
                state = SessionState.WALKING
            )
            
            _activeSession.value = initialSession
            sessionRepository.saveSession(initialSession)
            
            _currentState.value = SessionState.WALKING
            
            startObserving()
            
            return Result.Success(Unit)
        } finally {
            isTransitioning.set(false)
        }
    }

    override suspend fun pause(): Result<Unit, TrackingError> {
        if (_currentState.value != SessionState.WALKING) {
            return Result.Error(TrackingError.InvalidState)
        }
        _currentState.value = SessionState.PAUSED
        lastPauseTimestamp = clock.currentTimeMillis()
        
        trackingJob?.cancel()
        trackingJob = null
        
        _activeSession.value = _activeSession.value?.copy(state = SessionState.PAUSED)
        _activeSession.value?.let { sessionRepository.saveSession(it) }
        flushRoutePoints()
        
        return Result.Success(Unit)
    }

    override suspend fun resume(): Result<Unit, TrackingError> {
        if (_currentState.value != SessionState.PAUSED) {
            return Result.Error(TrackingError.InvalidState)
        }
        _currentState.value = SessionState.RESUMING
        
        val now = clock.currentTimeMillis()
        val pauseDuration = now - lastPauseTimestamp
        
        _activeSession.value = _activeSession.value?.let { session ->
            session.copy(
                state = SessionState.WALKING,
                pausedDuration = Duration(session.pausedDuration.millis + pauseDuration)
            )
        }
        
        _currentState.value = SessionState.WALKING
        _activeSession.value?.let { sessionRepository.saveSession(it) }
        
        startObserving()
        
        return Result.Success(Unit)
    }

    override suspend fun finish(): Result<Unit, TrackingError> {
        if (!isTransitioning.compareAndSet(false, true)) return Result.Error(TrackingError.InvalidState)
        try {
            if (_currentState.value == SessionState.IDLE) {
                return Result.Error(TrackingError.InvalidState)
            }
            _currentState.value = SessionState.FINISHING
            
            trackingJob?.cancel()
            trackingJob = null
            
            val endTime = clock.currentTimeMillis()
            val ms = measurementEngine.currentState
            
            _activeSession.value = _activeSession.value?.let { session ->
                val totalMillis = endTime - session.startTime
                session.copy(
                    endTime = endTime,
                    totalDuration = Duration(totalMillis),
                    activeDuration = Duration(ms.activeDurationMillis),
                    stepCount = ms.totalSteps,
                    distance = Distance(ms.totalDistanceMeters),
                    measurementMode = ms.currentMode,
                    confidence = ms.confidence,
                    averageSpeed = Speed(ms.averageSpeedMps),
                    state = SessionState.COMPLETED
                )
            }
            
            _activeSession.value?.let { sessionRepository.saveSession(it) }
            flushRoutePoints()

            _currentState.value = SessionState.COMPLETED
            currentSessionId = null
            
            return Result.Success(Unit)
        } finally {
            isTransitioning.set(false)
        }
    }

    override suspend fun reset(): Result<Unit, TrackingError> {
        if (_currentState.value != SessionState.COMPLETED && _currentState.value != SessionState.IDLE) {
            return Result.Error(TrackingError.InvalidState)
        }
        _currentState.value = SessionState.IDLE
        _activeSession.value = null
        currentSessionId = null
        return Result.Success(Unit)
    }

    private fun startObserving() {
        trackingJob?.cancel()
        trackingJob = scope.launch {
            if (stepDataSource.isAvailable) {
                launch {
                    stepDataSource.observeSteps()
                        .catch { }
                        .collect { event ->
                            measurementEngine.processObservation(StepObservation(event.timestamp, event.steps))
                            updateSessionFromMeasurement()
                        }
                }
            }

            if (locationDataSource.isAvailable) {
                launch {
                    locationDataSource.observeLocation()
                        .catch { }
                        .collect { event ->
                            measurementEngine.processObservation(
                                LocationObservation(
                                    event.timestamp, 
                                    event.latitude, 
                                    event.longitude, 
                                    event.horizontalAccuracy, 
                                    event.speed
                                )
                            )
                            
                            val validLoc = measurementEngine.currentState.lastValidLocation
                            if (validLoc != null && validLoc != previousLocation) {
                                previousLocation = validLoc
                                currentSessionId?.let { sId ->
                                    pendingRoutePoints.add(
                                        com.walknxt.app.data.local.entity.RoutePointEntity(
                                            sessionId = sId,
                                            timestamp = validLoc.timestamp,
                                            latitude = validLoc.latitude,
                                            longitude = validLoc.longitude,
                                            horizontalAccuracy = validLoc.horizontalAccuracy ?: 0f,
                                            speedMps = validLoc.speed ?: 0f
                                        )
                                    )
                                }
                            }
                            updateSessionFromMeasurement()
                        }
                }
            }
            
            if (motionDataSource.isAvailable) {
                launch {
                    motionDataSource.observeMotion()
                        .catch { }
                        .collect { event ->
                            measurementEngine.processObservation(MotionObservation(event.timestamp, event.x, event.y, event.z))
                        }
                }
            }
            
            launch {
                while (isActive) {
                    delay(30_000)
                    _activeSession.value?.let { sessionRepository.saveSession(it) }
                    flushRoutePoints()
                }
            }
        }
    }

    private suspend fun flushRoutePoints() {
        if (pendingRoutePoints.isNotEmpty()) {
            val pointsToSave = pendingRoutePoints.toList()
            pendingRoutePoints.clear()
            sessionRepository.saveRoutePoints(pointsToSave)
        }
    }

    private fun updateSessionFromMeasurement() {
        val ms = measurementEngine.currentState
        _activeSession.value = _activeSession.value?.copy(
            stepCount = ms.totalSteps,
            distance = Distance(ms.totalDistanceMeters),
            measurementMode = ms.currentMode,
            confidence = ms.confidence,
            activeDuration = Duration(ms.activeDurationMillis),
            averageSpeed = Speed(ms.averageSpeedMps)
        )
    }
}
