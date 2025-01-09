package com.ghostcat.deadzone.services

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.ghostcat.deadzone.models.GeoLocation
import com.ghostcat.deadzone.models.toGeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

class GeoLocationService(private val context: Context) {

    private val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): GeoLocation? {
        return try {
            // Check if location permissions are granted
            if (!hasLocationPermissions()) {
                return null // Return null if permissions are missing
            }

            val location = fusedLocationProviderClient.lastLocation.await()
            location?.toGeoLocation()
        } catch (e: Exception) {
            null // Handle errors (e.g., log them)
        }
    }

    private fun hasLocationPermissions(): Boolean {
        val fineLocationGranted = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationGranted || coarseLocationGranted
    }

//    suspend fun getUpdatedLocation(): GeoLocation? {
//
//    }
}