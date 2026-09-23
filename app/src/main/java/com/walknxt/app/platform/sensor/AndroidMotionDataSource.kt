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

class AndroidMotionDataSource(
    private val context: Context,
    private val clock: Clock
) : MotionDataSource {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    override val isAvailable: Boolean
        get() = accelSensor != null

    override fun observeMotion(): Flow<MotionEvent> = callbackFlow {
        if (accelSensor == null) {
            close(IllegalStateException("Accelerometer is not available."))
            return@callbackFlow
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    trySend(
                        MotionEvent(
                            timestamp = clock.currentTimeMillis(),
                            x = event.values[0],
                            y = event.values[1],
                            z = event.values[2]
                        )
                    )
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Handled in downstream analysis if required
            }
        }

        // SENSOR_DELAY_NORMAL is sufficient to gather evidence without eating battery
        sensorManager?.registerListener(listener, accelSensor, SensorManager.SENSOR_DELAY_NORMAL)

        awaitClose {
            sensorManager?.unregisterListener(listener)
        }
    }
}
