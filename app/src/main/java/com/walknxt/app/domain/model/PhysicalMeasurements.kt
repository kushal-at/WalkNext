package com.walknxt.app.domain.model

/** Represents distance in meters. */
@JvmInline
value class Distance(val meters: Double) {
    init {
        require(meters >= 0) { "Distance cannot be negative" }
    }
}

/** Represents duration in milliseconds. */
@JvmInline
value class Duration(val millis: Long) {
    init {
        require(millis >= 0) { "Duration cannot be negative" }
    }
}

/** Represents speed in meters per second. */
@JvmInline
value class Speed(val metersPerSecond: Double) {
    init {
        require(metersPerSecond >= 0) { "Speed cannot be negative" }
    }
}
