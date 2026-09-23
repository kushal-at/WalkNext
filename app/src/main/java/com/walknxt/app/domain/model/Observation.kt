package com.walknxt.app.domain.model

sealed class Observation {
    abstract val timestamp: Long
}

data class StepObservation(
    override val timestamp: Long,
    val sessionSteps: Int
) : Observation()

data class LocationObservation(
    override val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val horizontalAccuracy: Float?,
    val speed: Float?
) : Observation()

data class MotionObservation(
    override val timestamp: Long,
    val x: Float,
    val y: Float,
    val z: Float
) : Observation()
