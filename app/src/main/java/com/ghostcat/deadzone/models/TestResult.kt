package com.ghostcat.deadzone.models

import androidx.room.TypeConverter
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class TestResult(
    val connectionInfo: ConnectionInfo,
    val geoLocation: GeoLocation?,
)

class TestResultConverter {
    private val json = Json {ignoreUnknownKeys = true}

    @TypeConverter
    fun fromTestResultList(testReports: List<TestResult>): String {
        return json.encodeToString(testReports)
    }

    @TypeConverter
    fun toTestResultList(testResults: String): List<TestResult> {
        return json.decodeFromString(testResults)
    }
}