package com.ghostcat.deadzone.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(tableName = "test_reports")
data class TestReport(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @TypeConverters(TestReportConverter::class) val testResults: List<TestResult>,
)

class TestReportConverter {
    private val json = Json {ignoreUnknownKeys = true}

    @TypeConverters
    fun fromTestResultList(testReports: List<TestReport>): String {
        return json.encodeToString(testReports)
    }

    @TypeConverters
    fun toTestResultList(testReports: String): List<TestReport> {
        return json.decodeFromString(testReports)
    }
}