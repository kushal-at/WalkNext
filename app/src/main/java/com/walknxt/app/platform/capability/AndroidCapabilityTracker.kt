package com.walknxt.app.platform.capability

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.walknxt.app.platform.permission.CapabilityState
import com.walknxt.app.platform.permission.CapabilityTracker
import com.walknxt.app.platform.permission.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AndroidCapabilityTracker(private val context: Context) : CapabilityTracker {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    private val _capabilityState = MutableStateFlow(createSnapshot())
    override val capabilityState: StateFlow<CapabilityState> = _capabilityState.asStateFlow()

    override fun getStatus(permission: String): PermissionStatus {
        return if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            PermissionStatus.GRANTED
        } else {
            // Android doesn't allow easy synchronous detection of PERMANENTLY_DENIED without Activity context.
            // We default to DENIED here. The UI layer can refine this using shouldShowRequestPermissionRationale.
            PermissionStatus.DENIED
        }
    }

    override fun updateCapabilityState() {
        _capabilityState.value = createSnapshot()
    }

    private fun createSnapshot(): CapabilityState {
        val hasStepCounter = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null
        val hasAccelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
        
        val isGpsEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
        val isNetworkEnabled = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true

        val locPermission = getStatus(Manifest.permission.ACCESS_FINE_LOCATION)
        
        val actPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getStatus(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            PermissionStatus.GRANTED
        }

        return CapabilityState(
            isStepSensorAvailable = hasStepCounter,
            isLocationAvailable = isGpsEnabled || isNetworkEnabled,
            isMotionSensorAvailable = hasAccelerometer,
            locationPermissionStatus = locPermission,
            activityRecognitionPermissionStatus = actPermission
        )
    }
}
