package com.walknxt.app.platform.sensor

import kotlinx.coroutines.flow.Flow

data class MotionEvent(
    val timestamp: Long,
    val x: Float,
    val y: Float,
    val z: Float
)

interface MotionDataSource {
    val isAvailable: Boolean
    fun observeMotion(): Flow<MotionEvent>
}
