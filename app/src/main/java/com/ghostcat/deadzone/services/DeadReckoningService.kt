package com.ghostcat.deadzone.services

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.ghostcat.deadzone.models.GeoLocation
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class DeadReckoningService(context: Context, private var lastKnownLocation: GeoLocation?) {
    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var magnetometer: Sensor? = null

    private var stepCount = 0
    private var orientation = 0.0 // In radians

    private var currentLocation: GeoLocation? = lastKnownLocation

    init {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    fun startDeadReckoning(): GeoLocation? {
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(sensorEventListener, gyroscope, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_UI)
        return currentLocation
    }

    fun stopDeadReckoning() {
        sensorManager.unregisterListener(sensorEventListener)
    }

    private val sensorEventListener = object : SensorEventListener {
        private val gravity = FloatArray(3)
        private val geomagnetic = FloatArray(3)

        override fun onSensorChanged(event: SensorEvent?) {
            if (event == null) return

            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    System.arraycopy(event.values, 0, gravity, 0, event.values.size)
                    detectStep(event.values) // Update step count
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    System.arraycopy(event.values, 0, geomagnetic, 0, event.values.size)
                    updateOrientation()
                }
                Sensor.TYPE_GYROSCOPE -> {
                    // Use gyroscope for fine-grained orientation changes
                    orientation += event.values[2] * event.timestamp * 1e-9
                }
            }

            // Update the estimated location
            updateLocation()
        }

        fun updateLastKnownLocation(location: GeoLocation) {
            lastKnownLocation = location
            currentLocation = location
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        private fun detectStep(acceleration: FloatArray) {
            val magnitude = sqrt(
                (acceleration[0] * acceleration[0] +
                        acceleration[1] * acceleration[1] +
                        acceleration[2] * acceleration[2]).toDouble()
            )
            if (magnitude > STEP_THRESHOLD) {
                stepCount++
            }
        }

        private fun updateOrientation() {
            val r = FloatArray(9)
            val i = FloatArray(9)

            if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                val orientationAngles = FloatArray(3)
                SensorManager.getOrientation(r, orientationAngles)
                orientation = orientationAngles[0].toDouble() // Azimuth in radians
            }
        }

        private fun updateLocation() {
            // Assume a stride length of 0.8 meters (adjust based on user calibration)
            val strideLength = 0.8

            // Calculate displacement
            val dx = strideLength * cos(orientation)
            val dy = strideLength * sin(orientation)

            // Update location based on displacement
            val lat = currentLocation?.latitude ?: 0.0
            val lon = currentLocation?.longitude ?: 0.0

            currentLocation = GeoLocation(
                latitude = lat + (dy / EARTH_RADIUS) * (180 / Math.PI),
                longitude = lon + (dx / (EARTH_RADIUS * cos(lat * Math.PI / 180))) * (180 / Math.PI),
                accuracy = 10f, // Estimated accuracy
                timestamp = System.currentTimeMillis()
            )
        }
    }

    companion object {
        private const val STEP_THRESHOLD = 10.0 // Adjust as needed for step detection
        private const val EARTH_RADIUS = 6378137.0 // Earth's radius in meters
    }
}