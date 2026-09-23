package com.walknxt.app.domain.measurement

/**
 * Represents personal stride calibration.
 */
data class Calibration(
    val strideLengthMeters: Double = 0.74, // Default uncalibrated fallback
    val isCalibrated: Boolean = false
) {
    fun estimateDistance(steps: Int): Double {
        return steps * strideLengthMeters
    }
}
