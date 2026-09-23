package com.walknxt.app.domain.model

data class SessionSummary(
    val id: String,
    val startTime: Long,
    val duration: Duration,
    val steps: Int,
    val distance: Distance,
    val confidence: ConfidenceLevel
) {
    val caloriesBurned: Int
        get() {
            return if (distance.meters > 0) {
                (distance.meters * 0.07).toInt()
            } else {
                (steps * 0.04).toInt()
            }
        }

    val averagePaceMinPerKm: Double
        get() {
            val distKm = distance.meters / 1000.0
            val minutes = duration.millis / 60000.0
            return if (distKm > 0) minutes / distKm else 0.0
        }
}
