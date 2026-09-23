package com.walknxt.app.domain.model

data class AnalyticsSummary(
    val totalSteps: Int,
    val totalDistanceMeters: Double,
    val totalDurationMillis: Long,
    val sessionCount: Int
)
