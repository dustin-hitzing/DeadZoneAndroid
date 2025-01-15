package com.ghostcat.deadzone.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "test_reports")
//@TypeConverters(
//    TestResultConverter::class,
//    AddressInfoConverter::class,
//    GeoLocationConverter::class
//)
data class TestReport(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val testResults: List<TestResult>,
    val duration: Long,
)

