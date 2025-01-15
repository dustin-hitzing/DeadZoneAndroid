package com.ghostcat.deadzone.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ghostcat.deadzone.models.AddressInfoConverter
import com.ghostcat.deadzone.models.GeoLocationConverter
import com.ghostcat.deadzone.models.TestReport
import com.ghostcat.deadzone.models.TestResultConverter

@Database(entities = [TestReport::class], version = 1)
@TypeConverters(
    TestResultConverter::class,
    AddressInfoConverter::class,
    GeoLocationConverter::class
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun testReportDao(): TestReportDAO
}

