package com.ghostcat.deadzone.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ghostcat.deadzone.models.TestReportConverter
import com.ghostcat.deadzone.models.TestResult

@Database(entities = [TestResult::class], version = 1)
@TypeConverters(TestReportConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun testReportDao(): TestReportDAO
}

