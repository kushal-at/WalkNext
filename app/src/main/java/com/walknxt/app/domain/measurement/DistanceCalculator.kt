package com.walknxt.app.domain.measurement

import com.walknxt.app.domain.model.LocationObservation
import kotlin.math.*

object DistanceCalculator {

    private const val EARTH_RADIUS_METERS = 6371000.0

    /**
     * Calculates the geographic distance in meters between two coordinates using the Haversine formula.
     */
    fun calculateHaversineDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val rLat1 = Math.toRadians(lat1)
        val rLat2 = Math.toRadians(lat2)

        val a = sin(dLat / 2).pow(2.0) +
                sin(dLon / 2).pow(2.0) * cos(rLat1) * cos(rLat2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS_METERS * c
    }

    /**
     * Computes the implied speed in meters per second between two observations.
     */
    fun calculateImpliedSpeed(prev: LocationObservation, current: LocationObservation): Double {
        val distance = calculateHaversineDistance(
            prev.latitude, prev.longitude,
            current.latitude, current.longitude
        )
        val timeDiffSeconds = (current.timestamp - prev.timestamp) / 1000.0
        
        if (timeDiffSeconds <= 0.0) return 0.0
        return distance / timeDiffSeconds
    }
}
