package com.walknxt.app.domain.repository

import com.walknxt.app.core.result.Result
import com.walknxt.app.core.result.PersistenceError
import com.walknxt.app.domain.model.WalkSession
import com.walknxt.app.domain.model.SessionSummary
import com.walknxt.app.domain.model.AnalyticsSummary
import com.walknxt.app.data.local.entity.RoutePointEntity
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    suspend fun saveSession(session: WalkSession): Result<Unit, PersistenceError>
    suspend fun getSessionById(id: String): Result<WalkSession, PersistenceError>
    fun observeAllSessions(): Flow<List<SessionSummary>>
    
    // Deletion
    suspend fun deleteSession(id: String): Result<Unit, PersistenceError>
    suspend fun deleteAllSessions(): Result<Unit, PersistenceError>

    // Route checkpoints
    suspend fun saveRoutePoints(points: List<RoutePointEntity>): Result<Unit, PersistenceError>
    suspend fun getRoutePointsForSession(sessionId: String): Result<List<RoutePointEntity>, PersistenceError>
    fun observeRoutePointsForSession(sessionId: String): Flow<List<RoutePointEntity>>

    // Analytics
    fun observeAnalyticsBetween(startMillis: Long, endMillis: Long): Flow<AnalyticsSummary>
    fun observeAllTimeAnalytics(): Flow<AnalyticsSummary>
}
