package com.walknxt.app.platform.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.walknxt.app.platform.clock.Clock
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AndroidStepDataSource(
    private val context: Context,
    private val clock: Clock
) : StepDataSource {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    // Use the highly optimized hardware step counter instead of raw accelerometer math
    private val sensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    override val isAvailable: Boolean
        get() = sensor != null

    override fun observeSteps(): Flow<StepEvent> = callbackFlow {
        if (sensor == null) {
            close(IllegalStateException("Step Counter is not available on this device."))
            return@callbackFlow
        }

        var initialSteps = -1f
        var lastEmittedSteps = 0

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                    val hardwareSteps = event.values[0]
                    
                    if (initialSteps < 0) {
                        // First event defines the baseline for this tracking session
                        initialSteps = hardwareSteps
                        return
                    }

                    val currentSessionSteps = (hardwareSteps - initialSteps).toInt()
                    
                    if (currentSessionSteps > lastEmittedSteps) {
                        lastEmittedSteps = currentSessionSteps
                        trySend(StepEvent(timestamp = clock.currentTimeMillis(), steps = currentSessionSteps))
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Not needed for step counter
            }
        }

        // SENSOR_DELAY_UI is sufficient for step counting (batches events to save battery)
        sensorManager?.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)

        awaitClose {
            sensorManager?.unregisterListener(listener)
        }
    }
}
