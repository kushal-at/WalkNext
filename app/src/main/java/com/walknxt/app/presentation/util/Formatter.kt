package com.walknxt.app.presentation.util

import com.walknxt.app.domain.model.Distance
import com.walknxt.app.domain.model.Duration
import com.walknxt.app.domain.model.Speed
import java.util.Locale

object Formatter {
    fun formatDistance(distance: Distance, useImperial: Boolean = false): String {
        return if (useImperial) {
            val miles = distance.meters * 0.000621371
            String.format(Locale.getDefault(), "%.2f", miles)
        } else {
            val km = distance.meters / 1000.0
            String.format(Locale.getDefault(), "%.2f", km)
        }
    }

    fun distanceUnit(useImperial: Boolean = false): String {
        return if (useImperial) "mi" else "km"
    }

    fun formatDuration(duration: Duration): String {
        val totalSeconds = duration.millis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        
        return if (hours > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }

    fun formatSpeed(speed: Speed?, useImperial: Boolean = false): String {
        if (speed == null) return "—"
        return if (useImperial) {
            val mph = speed.metersPerSecond * 2.23694
            String.format(Locale.getDefault(), "%.1f", mph)
        } else {
            val kmh = speed.metersPerSecond * 3.6
            String.format(Locale.getDefault(), "%.1f", kmh)
        }
    }
    
    fun speedUnit(useImperial: Boolean = false): String {
        return if (useImperial) "mph" else "km/h"
    }
}
