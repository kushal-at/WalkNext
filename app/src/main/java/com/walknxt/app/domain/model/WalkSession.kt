package com.walknxt.app.domain.model

data class WalkSession(
    val id: String,
    val startTime: Long,
    val endTime: Long?,
    val activeDuration: Duration,
    val pausedDuration: Duration,
    val totalDuration: Duration,
    val stepCount: Int,
    val distance: Distance,
    val measurementMode: MeasurementMode,
    val averageSpeed: Speed?,
    val confidence: ConfidenceLevel,
    val state: SessionState
) {
    init {
        require(stepCount >= 0) { "Step count cannot be negative" }
        if (endTime != null) {
            require(endTime >= startTime) { "End time cannot precede start time" }
        }
    }

    val caloriesBurned: Int
        get() {
            // Rough estimation: 0.07 kcal per meter, fallback to 0.04 kcal per step
            return if (distance.meters > 0) {
                (distance.meters * 0.07).toInt()
            } else {
                (stepCount * 0.04).toInt()
            }
        }

    val averagePaceMinPerKm: Double
        get() {
            val distKm = distance.meters / 1000.0
            val minutes = activeDuration.millis / 60000.0
            return if (distKm > 0) minutes / distKm else 0.0
        }
}
