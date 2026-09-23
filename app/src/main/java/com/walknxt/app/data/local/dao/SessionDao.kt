package com.walknxt.app.data.local.dao

import androidx.room.*
import com.walknxt.app.data.local.entity.WalkSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WalkSessionEntity)

    @Query("SELECT * FROM walk_sessions WHERE id = :id")
    suspend fun getSessionById(id: String): WalkSessionEntity?

    @Query("SELECT * FROM walk_sessions ORDER BY startTime DESC")
    fun observeAllSessions(): Flow<List<WalkSessionEntity>>

    @Query("SELECT * FROM walk_sessions WHERE startTime >= :startMillis AND startTime <= :endMillis AND state = 'COMPLETED' ORDER BY startTime DESC")
    fun observeCompletedSessionsBetween(startMillis: Long, endMillis: Long): Flow<List<WalkSessionEntity>>

    @Query("SELECT * FROM walk_sessions WHERE state = 'COMPLETED'")
    fun observeAllCompleted(): Flow<List<WalkSessionEntity>>

    @Query("DELETE FROM walk_sessions WHERE id = :id")
    suspend fun deleteSession(id: String)
    
    @Query("DELETE FROM walk_sessions")
    suspend fun deleteAllSessions()
}
