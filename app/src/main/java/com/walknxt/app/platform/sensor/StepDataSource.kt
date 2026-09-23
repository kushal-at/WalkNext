package com.walknxt.app.platform.sensor

import kotlinx.coroutines.flow.Flow

data class StepEvent(val timestamp: Long, val steps: Int)

interface StepDataSource {
    val isAvailable: Boolean
    fun observeSteps(): Flow<StepEvent>
}
