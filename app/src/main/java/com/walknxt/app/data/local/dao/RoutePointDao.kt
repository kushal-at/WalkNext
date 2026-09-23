package com.walknxt.app.data.local.dao

import androidx.room.*
import com.walknxt.app.data.local.entity.RoutePointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutePointDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(points: List<RoutePointEntity>)

    @Query("SELECT * FROM route_points WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    suspend fun getPointsForSession(sessionId: String): List<RoutePointEntity>
    
    @Query("SELECT * FROM route_points WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun observePointsForSession(sessionId: String): Flow<List<RoutePointEntity>>
}
