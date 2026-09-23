package com.walknxt.app.domain.measurement

import com.walknxt.app.domain.model.ConfidenceLevel
import com.walknxt.app.domain.model.LocationObservation
import com.walknxt.app.domain.model.MeasurementMode

enum class ActivityClassification {
    WALKING,
    STATIONARY,
    VEHICLE_LIKELY,
    UNKNOWN
}

data class MeasurementState(
    val totalSteps: Int = 0,
    val totalDistanceMeters: Double = 0.0,
    
    val activeDurationMillis: Long = 0L,
    
    val currentMode: MeasurementMode = MeasurementMode.UNAVAILABLE,
    val confidence: ConfidenceLevel = ConfidenceLevel.UNAVAILABLE,
    val activityClassification: ActivityClassification = ActivityClassification.UNKNOWN,
    
    val lastValidLocation: LocationObservation? = null,
    val lastStepTimestamp: Long? = null,
    val consecutiveStationarySeconds: Long = 0L,
    val consecutiveVehicleSeconds: Long = 0L
) {
    val averageSpeedMps: Double
        get() = if (activeDurationMillis > 0) totalDistanceMeters / (activeDurationMillis / 1000.0) else 0.0

    val averagePaceSecondsPerMeter: Double
        get() = if (totalDistanceMeters > 0) (activeDurationMillis / 1000.0) / totalDistanceMeters else 0.0
}
