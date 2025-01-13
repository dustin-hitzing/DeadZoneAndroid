package com.ghostcat.deadzone.viewmodels

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostcat.deadzone.models.ConnectionInfo
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
) : ViewModel() {
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

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

    private suspend fun handleSuccess(connectionInfo: ConnectionInfo) {
        val geoLocation = geoLocationService.getCurrentLocation()
    }

    private suspend fun handleFailure(connectionInfo: ConnectionInfo) {

    }

}