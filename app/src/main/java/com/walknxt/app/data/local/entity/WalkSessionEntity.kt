package com.walknxt.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.walknxt.app.domain.model.ConfidenceLevel
import com.walknxt.app.domain.model.MeasurementMode
import com.walknxt.app.domain.model.SessionState

@Entity(tableName = "walk_sessions")
data class WalkSessionEntity(
    @PrimaryKey val id: String,
    val startTime: Long,
    val endTime: Long?,
    val activeDurationMillis: Long,
    val pausedDurationMillis: Long,
    val totalDurationMillis: Long,
    val stepCount: Int,
    val distanceMeters: Double,
    val measurementMode: MeasurementMode,
    val averageSpeedMps: Double?,
    val confidence: ConfidenceLevel,
    val state: SessionState,
    val timezoneOffsetSeconds: Int = java.util.TimeZone.getDefault().rawOffset / 1000
)
