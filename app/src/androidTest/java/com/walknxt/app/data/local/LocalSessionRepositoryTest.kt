package com.walknxt.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.walknxt.app.core.result.Result
import com.walknxt.app.data.local.entity.RoutePointEntity
import com.walknxt.app.data.repository.LocalSessionRepository
import com.walknxt.app.domain.model.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class LocalSessionRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: LocalSessionRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = LocalSessionRepository(db.sessionDao(), db.routePointDao())
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testSaveAndRetrieveSession() = runBlocking {
        val session = WalkSession(
            id = UUID.randomUUID().toString(),
            startTime = 1000L,
            endTime = 2000L,
            activeDuration = Duration(500),
            pausedDuration = Duration(0),
            totalDuration = Duration(1000),
            stepCount = 100,
            distance = Distance(80.0),
            measurementMode = MeasurementMode.ESTIMATED,
            averageSpeed = null,
            confidence = ConfidenceLevel.MEDIUM,
            state = SessionState.COMPLETED
        )

        val result = repository.saveSession(session)
        assertTrue(result is Result.Success)

        val retrieved = repository.getSessionById(session.id)
        assertTrue(retrieved is Result.Success)
        val loadedSession = (retrieved as Result.Success).value
        
        assertEquals(session.id, loadedSession.id)
        assertEquals(100, loadedSession.stepCount)
        assertEquals(80.0, loadedSession.distance.meters, 0.001)
    }

    @Test
    fun testRoutePointCascadeDelete() = runBlocking {
        val sessionId = UUID.randomUUID().toString()
        val session = WalkSession(
            id = sessionId,
            startTime = 1000L,
            endTime = 2000L,
            activeDuration = Duration(500),
            pausedDuration = Duration(0),
            totalDuration = Duration(1000),
            stepCount = 100,
            distance = Distance(80.0),
            measurementMode = MeasurementMode.ESTIMATED,
            averageSpeed = null,
            confidence = ConfidenceLevel.MEDIUM,
            state = SessionState.COMPLETED
        )
        repository.saveSession(session)

        val points = listOf(
            RoutePointEntity(sessionId = sessionId, timestamp = 1100L, latitude = 1.0, longitude = 1.0, horizontalAccuracy = 5f, speedMps = 1f),
            RoutePointEntity(sessionId = sessionId, timestamp = 1200L, latitude = 2.0, longitude = 2.0, horizontalAccuracy = 5f, speedMps = 1f)
        )
        repository.saveRoutePoints(points)

        val savedPoints = repository.getRoutePointsForSession(sessionId)
        assertTrue(savedPoints is Result.Success)
        assertEquals(2, (savedPoints as Result.Success).value.size)

        repository.deleteSession(sessionId)

        // Verify route points are deleted via CASCADE
        val remainingPoints = repository.getRoutePointsForSession(sessionId)
        assertTrue(remainingPoints is Result.Success)
        assertEquals(0, (remainingPoints as Result.Success).value.size)
    }

    @Test
    fun testAnalyticsAggregation() = runBlocking {
        val s1 = WalkSession(
            id = UUID.randomUUID().toString(),
            startTime = 1000L,
            endTime = 2000L,
            activeDuration = Duration(500),
            pausedDuration = Duration(0),
            totalDuration = Duration(1000),
            stepCount = 100,
            distance = Distance(80.0),
            measurementMode = MeasurementMode.ESTIMATED,
            averageSpeed = null,
            confidence = ConfidenceLevel.MEDIUM,
            state = SessionState.COMPLETED
        )
        val s2 = WalkSession(
            id = UUID.randomUUID().toString(),
            startTime = 5000L,
            endTime = 6000L,
            activeDuration = Duration(1000),
            pausedDuration = Duration(0),
            totalDuration = Duration(1000),
            stepCount = 200,
            distance = Distance(160.0),
            measurementMode = MeasurementMode.ESTIMATED,
            averageSpeed = null,
            confidence = ConfidenceLevel.MEDIUM,
            state = SessionState.COMPLETED
        )
        
        repository.saveSession(s1)
        repository.saveSession(s2)

        val analytics = repository.observeAllTimeAnalytics().first()
        assertEquals(2, analytics.sessionCount)
        assertEquals(300, analytics.totalSteps)
        assertEquals(240.0, analytics.totalDistanceMeters, 0.001)
        assertEquals(1500L, analytics.totalDurationMillis)
    }
}
