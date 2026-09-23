package com.walknxt.app.platform.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.walknxt.app.platform.clock.Clock
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AndroidLocationDataSource(
    private val context: Context,
    private val clock: Clock
) : LocationDataSource {

    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    override val isAvailable: Boolean
        get() = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
                locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true

    @SuppressLint("MissingPermission") // Caller (Coordinator) handles permissions
    override fun observeLocation(): Flow<LocationEvent> = callbackFlow {
        if (!isAvailable) {
            close(IllegalStateException("Location services are disabled on this device."))
            return@callbackFlow
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
            .setMinUpdateIntervalMillis(2000L)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEach { location ->
                    if (isValidRawLocation(location)) {
                        trySend(
                            LocationEvent(
                                timestamp = clock.currentTimeMillis(), // Aligning to unified app clock
                                latitude = location.latitude,
                                longitude = location.longitude,
                                horizontalAccuracy = if (location.hasAccuracy()) location.accuracy else null,
                                speed = if (location.hasSpeed()) location.speed else null
                            )
                        )
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            request,
            callback,
            Looper.getMainLooper() // Use main looper to avoid thread blocking; Flow buffers correctly
        ).addOnFailureListener { e ->
            close(e)
        }

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    private fun isValidRawLocation(location: Location): Boolean {
        if (location.latitude == 0.0 && location.longitude == 0.0) return false
        if (location.latitude < -90.0 || location.latitude > 90.0) return false
        if (location.longitude < -180.0 || location.longitude > 180.0) return false
        // We reject totally broken lat/lng, but leave accuracy checks to Prompt 6
        return true
    }
}
