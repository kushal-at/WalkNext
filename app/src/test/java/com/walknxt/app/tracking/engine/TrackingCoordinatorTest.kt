package com.walknxt.app.tracking.engine

import com.walknxt.app.core.result.Result
import com.walknxt.app.core.result.TrackingError
import com.walknxt.app.domain.model.*
import com.walknxt.app.domain.repository.SessionRepository
import com.walknxt.app.platform.clock.Clock
import com.walknxt.app.platform.location.LocationDataSource
import com.walknxt.app.platform.location.LocationEvent
import com.walknxt.app.platform.permission.CapabilityState
import com.walknxt.app.platform.permission.CapabilityTracker
import com.walknxt.app.platform.permission.PermissionStatus
import com.walknxt.app.platform.sensor.MotionDataSource
import com.walknxt.app.platform.sensor.MotionEvent
import com.walknxt.app.platform.sensor.StepDataSource
import com.walknxt.app.platform.sensor.StepEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import kotlinx.coroutines.cancel
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TrackingCoordinatorTest {

    private lateinit var testClock: TestClock
    private lateinit var fakeStepSource: FakeStepDataSource
    private lateinit var fakeMotionSource: FakeMotionDataSource
    private lateinit var fakeLocationSource: FakeLocationDataSource
    private lateinit var fakeCapabilityTracker: FakeCapabilityTracker
    private lateinit var fakeSessionRepository: FakeSessionRepository
    private lateinit var coordinator: TrackingCoordinator
    private lateinit var testScope: TestScope

    @Before
    fun setup() {
        testClock = TestClock()
        fakeStepSource = FakeStepDataSource()
        fakeMotionSource = FakeMotionDataSource()
        fakeLocationSource = FakeLocationDataSource()
        fakeCapabilityTracker = FakeCapabilityTracker()
        fakeSessionRepository = FakeSessionRepository()
        testScope = TestScope(UnconfinedTestDispatcher())

        coordinator = TrackingCoordinator(
            testClock,
            fakeStepSource,
            fakeMotionSource,
            fakeLocationSource,
            fakeCapabilityTracker,
            fakeSessionRepository,
            testScope
        )
    }

    @After
    fun tearDown() {
        testScope.cancel()
    }

    @Test
    fun testStartTracking_Success() = testScope.runTest {
        val result = coordinator.start()
        assertTrue(result is Result.Success)
        assertEquals(SessionState.WALKING, coordinator.currentState.value)
        assertNotNull(coordinator.activeSession.value)
        coordinator.finish() // Clean up background jobs
    }

    @Test
    fun testStartTracking_PermissionDenied() = testScope.runTest {
        fakeCapabilityTracker.state = fakeCapabilityTracker.state.copy(
            activityRecognitionPermissionStatus = PermissionStatus.DENIED
        )
        val result = coordinator.start()
        assertTrue(result is Result.Error)
        assertEquals(TrackingError.PermissionRequired, (result as Result.Error).error)
        assertEquals(SessionState.IDLE, coordinator.currentState.value)
    }

    @Test
    fun testStartTracking_SensorsUnavailable() = testScope.runTest {
        fakeCapabilityTracker.state = fakeCapabilityTracker.state.copy(
            isStepSensorAvailable = false,
            isLocationAvailable = false
        )
        val result = coordinator.start()
        assertTrue(result is Result.Error)
        assertEquals(TrackingError.SensorUnavailable, (result as Result.Error).error)
    }

    @Test
    fun testPauseResume() = testScope.runTest {
        coordinator.start()
        assertEquals(SessionState.WALKING, coordinator.currentState.value)
        
        testClock.time += 1000L
        coordinator.pause()
        assertEquals(SessionState.PAUSED, coordinator.currentState.value)
        
        testClock.time += 5000L // Paused for 5 seconds
        coordinator.resume()
        assertEquals(SessionState.WALKING, coordinator.currentState.value)
        
        val activeSession = coordinator.activeSession.value!!
        assertEquals(5000L, activeSession.pausedDuration.millis)
        coordinator.finish() // Clean up background jobs
    }

    @Test
    fun testFinishTracking() = testScope.runTest {
        coordinator.start()
        val startSession = coordinator.activeSession.value!!
        
        testClock.time += 10000L
        coordinator.finish()
        
        assertEquals(SessionState.COMPLETED, coordinator.currentState.value)
        val finalSession = fakeSessionRepository.savedSessions.last()
        assertEquals(10000L, finalSession.totalDuration.millis)
        assertEquals(0L, finalSession.activeDuration.millis) // No observations were sent
        assertNotNull(finalSession.endTime)
    }
}

class TestClock : Clock {
    var time = 0L
    override fun currentTimeMillis(): Long = time
    override fun elapsedRealtime(): Long = time
}

class FakeStepDataSource : StepDataSource {
    override var isAvailable = true
    override fun observeSteps(): Flow<StepEvent> = emptyFlow()
}

class FakeMotionDataSource : MotionDataSource {
    override var isAvailable = true
    override fun observeMotion(): Flow<MotionEvent> = emptyFlow()
}

class FakeLocationDataSource : LocationDataSource {
    override var isAvailable = true
    override fun observeLocation(): Flow<LocationEvent> = emptyFlow()
}

class FakeCapabilityTracker : CapabilityTracker {
    var state = CapabilityState(
        isStepSensorAvailable = true,
        isLocationAvailable = true,
        isMotionSensorAvailable = true,
        locationPermissionStatus = PermissionStatus.GRANTED,
        activityRecognitionPermissionStatus = PermissionStatus.GRANTED
    )
    override val capabilityState: StateFlow<CapabilityState>
        get() = MutableStateFlow(state).asStateFlow()

    override fun getStatus(permission: String): PermissionStatus = PermissionStatus.GRANTED
    override fun updateCapabilityState() {}
}

class FakeSessionRepository : SessionRepository {
    val savedSessions = mutableListOf<WalkSession>()
    
    override suspend fun saveSession(session: WalkSession): Result<Unit, com.walknxt.app.core.result.PersistenceError> {
        savedSessions.add(session)
        return Result.Success(Unit)
    }
    
    override suspend fun getSessionById(id: String): Result<WalkSession, com.walknxt.app.core.result.PersistenceError> = Result.Success(savedSessions.first())
    override fun observeAllSessions(): Flow<List<SessionSummary>> = emptyFlow()
    override suspend fun deleteSession(id: String): Result<Unit, com.walknxt.app.core.result.PersistenceError> = Result.Success(Unit)
    
    override suspend fun deleteAllSessions(): Result<Unit, com.walknxt.app.core.result.PersistenceError> = Result.Success(Unit)
    override suspend fun saveRoutePoints(points: List<com.walknxt.app.data.local.entity.RoutePointEntity>): Result<Unit, com.walknxt.app.core.result.PersistenceError> = Result.Success(Unit)
    override suspend fun getRoutePointsForSession(sessionId: String): Result<List<com.walknxt.app.data.local.entity.RoutePointEntity>, com.walknxt.app.core.result.PersistenceError> = Result.Success(emptyList())
    override fun observeAnalyticsBetween(startMillis: Long, endMillis: Long): Flow<AnalyticsSummary> = emptyFlow()
    override fun observeAllTimeAnalytics(): Flow<AnalyticsSummary> = emptyFlow()
}
