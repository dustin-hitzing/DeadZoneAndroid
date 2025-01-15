package com.ghostcat.deadzone.models

import androidx.room.TypeConverter
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class AddressInfo(
    val countryCode: String?,
    val countryName: String?,
    val phone: String?,
    val postalCode: String?,
    val locality: String?,
    val premises: String?,
    val subLocality: String?,
)

class AddressInfoConverter {
    private val json = Json {ignoreUnknownKeys = true}

    @TypeConverter
    fun fromAddressInfo(addressInfo: AddressInfo): String {
        return json.encodeToString(addressInfo)
    }

    @TypeConverter
    fun toAddressInfo(addressInfo: String): AddressInfo? {
        return json.decodeFromString(addressInfo)
    }
}