package com.walknxt.app.data.repository

import com.walknxt.app.core.result.Result
import com.walknxt.app.core.result.PersistenceError
import com.walknxt.app.data.local.dao.RoutePointDao
import com.walknxt.app.data.local.dao.SessionDao
import com.walknxt.app.data.local.entity.RoutePointEntity
import com.walknxt.app.data.local.entity.WalkSessionEntity
import com.walknxt.app.domain.model.*
import com.walknxt.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalSessionRepository(
    private val sessionDao: SessionDao,
    private val routePointDao: RoutePointDao? = null
) : SessionRepository {

    override suspend fun saveSession(session: WalkSession): Result<Unit, PersistenceError> {
        return try {
            sessionDao.insertSession(session.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(PersistenceError.DatabaseError)
        }
    }

    override suspend fun getSessionById(id: String): Result<WalkSession, PersistenceError> {
        return try {
            val entity = sessionDao.getSessionById(id)
            if (entity != null) {
                Result.Success(entity.toDomain())
            } else {
                Result.Error(PersistenceError.NotFound)
            }
        } catch (e: Exception) {
            Result.Error(PersistenceError.DatabaseError)
        }
    }

    override fun observeAllSessions(): Flow<List<SessionSummary>> {
        return sessionDao.observeAllSessions().map { entities ->
            entities.map { it.toSummary() }
        }
    }

    override suspend fun deleteSession(id: String): Result<Unit, PersistenceError> {
        return try {
            sessionDao.deleteSession(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(PersistenceError.DatabaseError)
        }
    }
    
    override suspend fun deleteAllSessions(): Result<Unit, PersistenceError> {
        return try {
            sessionDao.deleteAllSessions()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(PersistenceError.DatabaseError)
        }
    }

    override suspend fun saveRoutePoints(points: List<RoutePointEntity>): Result<Unit, PersistenceError> {
        return try {
            routePointDao?.insertAll(points)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(PersistenceError.DatabaseError)
        }
    }

    override suspend fun getRoutePointsForSession(sessionId: String): Result<List<RoutePointEntity>, PersistenceError> {
        return try {
            val points = routePointDao?.getPointsForSession(sessionId) ?: emptyList()
            Result.Success(points)
        } catch (e: Exception) {
            Result.Error(PersistenceError.DatabaseError)
        }
    }

    override fun observeRoutePointsForSession(sessionId: String): Flow<List<RoutePointEntity>> {
        return routePointDao?.observePointsForSession(sessionId) ?: kotlinx.coroutines.flow.emptyFlow()
    }

    override fun observeAnalyticsBetween(startMillis: Long, endMillis: Long): Flow<AnalyticsSummary> {
        return sessionDao.observeCompletedSessionsBetween(startMillis, endMillis).map { sessions ->
            calculateAnalytics(sessions)
        }
    }

    override fun observeAllTimeAnalytics(): Flow<AnalyticsSummary> {
        return sessionDao.observeAllCompleted().map { sessions ->
            calculateAnalytics(sessions)
        }
    }

    private fun calculateAnalytics(sessions: List<WalkSessionEntity>): AnalyticsSummary {
        var steps = 0
        var distance = 0.0
        var duration = 0L
        for (s in sessions) {
            steps += s.stepCount
            distance += s.distanceMeters
            duration += s.activeDurationMillis
        }
        return AnalyticsSummary(
            totalSteps = steps,
            totalDistanceMeters = distance,
            totalDurationMillis = duration,
            sessionCount = sessions.size
        )
    }

    private fun WalkSession.toEntity() = WalkSessionEntity(
        id = id,
        startTime = startTime,
        endTime = endTime,
        activeDurationMillis = activeDuration.millis,
        pausedDurationMillis = pausedDuration.millis,
        totalDurationMillis = totalDuration.millis,
        stepCount = stepCount,
        distanceMeters = distance.meters,
        measurementMode = measurementMode,
        averageSpeedMps = averageSpeed?.metersPerSecond,
        confidence = confidence,
        state = state
    )

    private fun WalkSessionEntity.toDomain() = WalkSession(
        id = id,
        startTime = startTime,
        endTime = endTime,
        activeDuration = Duration(activeDurationMillis),
        pausedDuration = Duration(pausedDurationMillis),
        totalDuration = Duration(totalDurationMillis),
        stepCount = stepCount,
        distance = Distance(distanceMeters),
        measurementMode = measurementMode,
        averageSpeed = averageSpeedMps?.let { Speed(it) },
        confidence = confidence,
        state = state
    )

    private fun WalkSessionEntity.toSummary() = SessionSummary(
        id = id,
        startTime = startTime,
        duration = Duration(totalDurationMillis),
        steps = stepCount,
        distance = Distance(distanceMeters),
        confidence = confidence
    )
}
