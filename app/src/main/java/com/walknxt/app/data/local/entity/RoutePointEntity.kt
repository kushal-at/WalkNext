package com.walknxt.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "route_points",
    foreignKeys = [
        ForeignKey(
            entity = WalkSessionEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("sessionId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class RoutePointEntity(
    @PrimaryKey(autoGenerate = true) val pointId: Long = 0,
    val sessionId: String,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val horizontalAccuracy: Float,
    val speedMps: Float
)
