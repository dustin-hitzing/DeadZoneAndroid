package com.ghostcat.deadzone.models

import android.location.Location

data class GeoLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val timestamp: Long
)

fun Location.toGeoLocation(): GeoLocation {
    return GeoLocation(
        latitude = latitude,
        longitude = longitude,
        accuracy = accuracy,
        timestamp = time
    )
}