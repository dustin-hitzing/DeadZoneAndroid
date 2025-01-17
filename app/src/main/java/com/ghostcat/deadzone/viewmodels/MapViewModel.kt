package com.ghostcat.deadzone.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostcat.deadzone.database.TestReportDAO
import com.ghostcat.deadzone.models.TestReport
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val testReportDao: TestReportDAO,
) : ViewModel() {
    private val _testReports = MutableStateFlow<List<TestReport>>(emptyList())
    val testReports: StateFlow<List<TestReport>> = _testReports

    private val _selectedTestReport = MutableStateFlow<TestReport?>(null)
    val selectedTestReport: StateFlow<TestReport?> = _selectedTestReport

    fun getReports() {
        viewModelScope.launch {
            _testReports.value = testReportDao.getAllTestReports()
                .sortedByDescending { it.testResults[0].geoLocation?.timestamp }
        }
    }

    fun selectTestReport(testReport: TestReport) {
        _selectedTestReport.value = testReport
    }

}