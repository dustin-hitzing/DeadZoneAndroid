package com.ghostcat.deadzone.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ghostcat.deadzone.models.TestReport

@Dao
interface TestReportDAO {
    @Insert
    suspend fun insertTestReport(testReport: TestReport)

    @Query("SELECT * FROM test_reports")
    suspend fun getAllTestReports(): List<TestReport>

    @Query("DELETE FROM test_reports")
    suspend fun deleteAllTestReports()
}

