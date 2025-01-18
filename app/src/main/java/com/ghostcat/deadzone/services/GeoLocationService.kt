package com.ghostcat.deadzone.services

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.os.Build
import androidx.core.app.ActivityCompat
import com.ghostcat.deadzone.models.AddressInfo
import com.ghostcat.deadzone.models.GeoLocation
import com.ghostcat.deadzone.models.toGeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import kotlin.math.*

class GeoLocationService(private val context: Context) : SensorEventListener {

    private val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var lastGoodGeoLocation: GeoLocation? = null
    private var secondLastGoodGeoLocation: GeoLocation? = null

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accumulatedDisplacement =
        0.0    // in meters, from basic integration of acceleration
    private var currentHeading =
        0.0             // in degrees, updated from gyroscope + magnetometer filter
    private var lastSensorUpdateTime: Long = 0L

    // We keep our simple Kalman filter for heading (for short-term correction) and also instantiate a full Kalman filter below.
    private var headingKalmanFilter: KalmanFilter? = null

    // Simple Kalman filter for heading (as used previously)
    private class KalmanFilter(
        var q: Double,
        var r: Double,
        var p: Double,
        var k: Double,
        var x: Double
    ) {
        fun update(measurement: Double): Double {
            p += q
            k = p / (p + r)
            x += k * (measurement - x)
            p *= (1 - k)
            return x
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): GeoLocation? {
        return try {
            if (!hasLocationPermissions()) return null
            val location = fusedLocationProviderClient.lastLocation.await()
            val geolocation = location?.toGeoLocation().apply {
                this?.addressInfo = getAddressFromCoordinates(location.latitude, location.longitude)
            }
            geolocation?.let {
                secondLastGoodGeoLocation = lastGoodGeoLocation
                lastGoodGeoLocation = it
            }
            geolocation
        } catch (e: Exception) {
            null
        }
    }

    private fun hasLocationPermissions(): Boolean {
        val fine = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    private suspend fun getAddressFromCoordinates(
        latitude: Double,
        longitude: Double
    ): AddressInfo {
        val geocoder = Geocoder(context)
        return suspendCancellableCoroutine { cont ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    val address = addresses.firstOrNull()
                    val addressInfo = AddressInfo(
                        countryCode = address?.countryCode,
                        countryName = address?.countryName,
                        phone = address?.phone,
                        postalCode = address?.postalCode,
                        locality = address?.locality,
                        premises = address?.premises,
                        subLocality = address?.subLocality
                    )
                    cont.resumeWith(Result.success(addressInfo))
                }
            } else {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                val address = addresses?.firstOrNull()
                val addressInfo = AddressInfo(
                    countryCode = address?.countryCode,
                    countryName = address?.countryName,
                    phone = address?.phone,
                    postalCode = address?.postalCode,
                    locality = address?.locality,
                    premises = address?.premises,
                    subLocality = address?.subLocality
                )
                cont.resumeWith(Result.success(addressInfo))
            }
        }
    }

    /**
     * Uses the full Kalman filter to fuse inertial sensor data over an integration interval
     * and predict a new position relative to the last known GPS fix.
     */
    suspend fun getRobustDeadReckonedPositionFullKalman(
        integrationIntervalMillis: Long = 5000L
    ): GeoLocation? {
        val referenceLocation = lastGoodGeoLocation ?: return null

        // Reset accumulators.
        accumulatedDisplacement = 0.0
        lastSensorUpdateTime = System.currentTimeMillis()
        currentHeading = 0.0
        headingKalmanFilter = null

        // Initialize the full Kalman filter with a time step based on integration interval.
        val dt = integrationIntervalMillis / 1000.0
        val fullKalman = FullKalmanFilter(dt)

        // Register sensors.
        val accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        val gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, magSensor, SensorManager.SENSOR_DELAY_GAME)

        // Integrate sensor data over the interval.
        try {
            withTimeout(integrationIntervalMillis) {
                delay(integrationIntervalMillis)
            }
        } finally {
            sensorManager.unregisterListener(this)
        }

        // For the purposes of a measurement update, we treat the accumulated (x, y) displacement
        // as measured from our inertial integration. We derive these from the current heading and displacement:
        val measuredX = accumulatedDisplacement * cos(Math.toRadians(currentHeading))
        val measuredY = accumulatedDisplacement * sin(Math.toRadians(currentHeading))
        fullKalman.update(doubleArrayOf(measuredX, measuredY))

        val (estimatedX, estimatedY) = fullKalman.getPosition()

        // Now, convert the estimated displacement (in meters) to a change in lat/lon based on the reference GPS fix.
        // Compute the heading for the displacement.
        val estimatedHeading = (Math.toDegrees(atan2(estimatedY, estimatedX)) + 360) % 360
        // Compute the distance as the Euclidean norm.
        val estimatedDistance = sqrt(estimatedX.pow(2) + estimatedY.pow(2))

        val (predictedLat, predictedLon) = calculateDestinationCoordinates(
            referenceLocation.latitude,
            referenceLocation.longitude,
            estimatedHeading,
            estimatedDistance
        )

        return referenceLocation.copy(
            latitude = predictedLat,
            longitude = predictedLon,
            addressInfo = referenceLocation.addressInfo
        )
    }

    private fun calculateDistanceMeters(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double
    ): Double {
        val R = 6371000.0
        val startLatRad = Math.toRadians(startLatitude)
        val endLatRad = Math.toRadians(endLatitude)
        val deltaLat = Math.toRadians(endLatitude - startLatitude)
        val deltaLon = Math.toRadians(endLongitude - startLongitude)
        val a = sin(deltaLat / 2).pow(2.0) +
                cos(startLatRad) * cos(endLatRad) * sin(deltaLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    private fun calculateBearingDegrees(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double
    ): Double {
        val startLatRad = Math.toRadians(startLatitude)
        val endLatRad = Math.toRadians(endLatitude)
        val deltaLon = Math.toRadians(endLongitude - startLongitude)
        val y = sin(deltaLon) * cos(endLatRad)
        val x =
            cos(startLatRad) * sin(endLatRad) - sin(startLatRad) * cos(endLatRad) * cos(deltaLon)
        val bearingRad = atan2(y, x)
        return (Math.toDegrees(bearingRad) + 360) % 360
    }

    private fun calculateDestinationCoordinates(
        startLatitude: Double,
        startLongitude: Double,
        bearingDegrees: Double,
        distanceMeters: Double
    ): Pair<Double, Double> {
        val R = 6371000.0
        val angularDistance = distanceMeters / R
        val bearingRad = Math.toRadians(bearingDegrees)
        val startLatRad = Math.toRadians(startLatitude)
        val startLonRad = Math.toRadians(startLongitude)

        val destLatRad = asin(
            sin(startLatRad) * cos(angularDistance) +
                    cos(startLatRad) * sin(angularDistance) * cos(bearingRad)
        )
        val destLonRad = startLonRad + atan2(
            sin(bearingRad) * sin(angularDistance) * cos(startLatRad),
            cos(angularDistance) - sin(startLatRad) * sin(destLatRad)
        )

        return Pair(Math.toDegrees(destLatRad), Math.toDegrees(destLonRad))
    }

    // ---------- Sensor Event Handling ----------

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        val currentTime = System.currentTimeMillis()
        val dt = (currentTime - lastSensorUpdateTime) / 1000.0
        lastSensorUpdateTime = currentTime

        when (event.sensor.type) {
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                val ax = event.values[0]
                val ay = event.values[1]
                val accelerationMag = sqrt(ax * ax + ay * ay)
                // Basic integration for displacement (assuming zero initial velocity).
                val displacement = 0.5 * accelerationMag * dt * dt
                accumulatedDisplacement += displacement
            }

            Sensor.TYPE_GYROSCOPE -> {
                val angularChangeRad = event.values[2] * dt
                currentHeading = (currentHeading + Math.toDegrees(angularChangeRad)) % 360.0
                if (currentHeading < 0) currentHeading += 360.0
            }

            Sensor.TYPE_MAGNETIC_FIELD -> {
                val magX = event.values[0]
                val magY = event.values[1]
                val rawHeading = (Math.toDegrees(atan2(magY, magX).toDouble()) + 360) % 360
                if (headingKalmanFilter == null) {
                    headingKalmanFilter =
                        KalmanFilter(q = 0.1, r = 1.0, p = 1.0, k = 0.0, x = rawHeading)
                } else {
                    headingKalmanFilter!!.update(rawHeading)
                }
                currentHeading = headingKalmanFilter!!.x
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used.
    }
}
