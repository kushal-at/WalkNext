package com.walknxt.app.platform.location

import kotlinx.coroutines.flow.Flow

data class LocationEvent(
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val horizontalAccuracy: Float?,
    val speed: Float?
)

interface LocationDataSource {
    val isAvailable: Boolean
    fun observeLocation(): Flow<LocationEvent>
}
