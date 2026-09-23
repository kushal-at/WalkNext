package com.walknxt.app.domain.model

data class CalibrationProfile(
    val estimatedStrideMeters: Double,
    val confidence: ConfidenceLevel,
    val lastUpdatedTimestamp: Long
)
