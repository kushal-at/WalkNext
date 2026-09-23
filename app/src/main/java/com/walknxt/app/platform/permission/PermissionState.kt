package com.walknxt.app.platform.permission

import kotlinx.coroutines.flow.StateFlow

enum class PermissionStatus {
    NOT_REQUESTED,
    GRANTED,
    DENIED,
    PERMANENTLY_DENIED
}

data class CapabilityState(
    val isStepSensorAvailable: Boolean,
    val isLocationAvailable: Boolean,
    val isMotionSensorAvailable: Boolean,
    val locationPermissionStatus: PermissionStatus,
    val activityRecognitionPermissionStatus: PermissionStatus
)

interface CapabilityTracker {
    val capabilityState: StateFlow<CapabilityState>
    fun getStatus(permission: String): PermissionStatus
    fun updateCapabilityState()
}
