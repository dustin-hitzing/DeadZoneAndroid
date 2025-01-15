package com.ghostcat.deadzone.services

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import androidx.core.app.ActivityCompat
import com.ghostcat.deadzone.models.AddressInfo
import com.ghostcat.deadzone.models.GeoLocation
import com.ghostcat.deadzone.models.toGeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
            val geolocation = location?.toGeoLocation()
            geolocation?.addressInfo = getAddressFromCoordinates(location.latitude, location.longitude)
            geolocation
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

    private suspend fun getAddressFromCoordinates(latitude: Double, longitude: Double): AddressInfo {
        val geocoder = Geocoder(context)
        return suspendCancellableCoroutine { continuation ->
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
                    continuation.resumeWith(Result.success(addressInfo))
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
                continuation.resumeWith(Result.success(addressInfo))
            }
        }
    }

//    suspend fun getUpdatedLocation(): GeoLocation? {
//
//    }
}