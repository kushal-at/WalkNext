package com.walknxt.app.domain.model

data class RoutePoint(
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val horizontalAccuracy: Float?,
    val altitude: Double?,
    val speed: Float?
)

data class Route(
    val sessionId: String,
    val points: List<RoutePoint>
)
