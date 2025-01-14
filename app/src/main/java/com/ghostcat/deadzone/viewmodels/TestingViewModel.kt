package com.ghostcat.deadzone.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostcat.deadzone.database.TestReportDAO
import com.ghostcat.deadzone.models.ConnectionInfo
import com.ghostcat.deadzone.models.TestReport
import com.ghostcat.deadzone.models.TestResult
import com.ghostcat.deadzone.services.ConnectivityChecker
import com.ghostcat.deadzone.services.GeoLocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestingViewModel @Inject constructor(
    private val connectivityChecker: ConnectivityChecker,
    private val geoLocationService: GeoLocationService,
    private val testReportDao: TestReportDAO,
) : ViewModel() {
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _successes = MutableStateFlow(0)
    val successes: StateFlow<Int> = _successes

    private  val _failures = MutableStateFlow(0)
    val failures: StateFlow<Int> = _failures

    private var testResults: MutableList<TestResult> = mutableListOf()

    private var monitoringJob: Job? = null

    init {
        monitorConnectivity()
    }

    private fun monitorConnectivity() {
        monitoringJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            while (isActive) {
                val elapsedTime = System.currentTimeMillis() - startTime
                if (elapsedTime >= (60 * 60 * 1000)) {
                    stopMonitoring()
                    break
                }
                val connectionInfo = connectivityChecker.getConnectionInfo()
                _isConnected.value = connectionInfo.isConnected
                if (_isConnected.value) {
                    handleSuccess(connectionInfo)
                } else {
                    handleFailure(connectionInfo)
                }

                delay(5000L)
            }
        }
    }

    fun stopMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = null
    }

    fun endTesting() {
        stopMonitoring()
        viewModelScope.launch {
            var report = TestReport(
                testResults = testResults
            )
            testReportDao.insertTestReport(report)
        }
    }

    private suspend fun handleSuccess(connectionInfo: ConnectionInfo) {
        _successes.value += 1
        val geoLocation = geoLocationService.getCurrentLocation()
        val testResult = TestResult(
            connectionInfo = connectionInfo,
            geoLocation = geoLocation
        )
        testResults.add(testResult)
    }

    private suspend fun handleFailure(connectionInfo: ConnectionInfo) {
        _failures.value += 1
        val testResult = TestResult(
            connectionInfo = connectionInfo,
            geoLocation = null,
        )
        testResults.add(testResult)
    }

}