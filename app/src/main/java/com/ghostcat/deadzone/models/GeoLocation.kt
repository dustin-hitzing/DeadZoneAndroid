package com.ghostcat.deadzone.models

import android.location.Location
import androidx.room.TypeConverter
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class GeoLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val timestamp: Long,
    var addressInfo: AddressInfo? = null
)

class GeoLocationConverter {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromGeoLocation(geoLocation: GeoLocation?): String {
        return json.encodeToString(geoLocation)
    }

    @TypeConverter
    fun toGeoLocation(geoLocationJson: String?): GeoLocation? {
        return geoLocationJson?.let { json.decodeFromString(it) }
    }
}

fun Location.toGeoLocation(): GeoLocation {
    return GeoLocation(
        latitude = latitude,
        longitude = longitude,
        accuracy = accuracy,
        timestamp = time
    )
}