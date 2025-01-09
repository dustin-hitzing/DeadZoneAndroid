package com.ghostcat.deadzone.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import com.ghostcat.deadzone.models.ConnectionInfo
import java.net.Inet4Address
import java.net.NetworkInterface

class ConnectivityChecker(private val context: Context) {

    fun getConnectionInfo(): ConnectionInfo {
        val isConnected = isConnectedToInternet()
        val networkType = getNetworkType()
        val ipAddress = getIpAddress()
        val providerName = getProviderName()
        val wifiInfo = if (networkType == "Wi-Fi") {
            getAdditionalWifiInfo()
        } else {
            "N/A"
        }

        return ConnectionInfo(
            isConnected = isConnected,
            networkType = networkType,
            ipAddress = ipAddress,
            providerName = providerName,
            wifiInfo = wifiInfo
        )

    }

    fun isConnectedToInternet() : Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun getNetworkType(): String {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return when {
            capabilities == null -> "No Connection"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Unknown"
        }
    }

    private fun getProviderName(): String {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.networkOperatorName ?: "Unknown"
    }

    private fun getIpAddress(): String {
        return try {
            NetworkInterface.getNetworkInterfaces()
                .toList()
                .flatMap { it.inetAddresses.toList() }
                .firstOrNull { !it.isLoopbackAddress && it is Inet4Address }
                ?.hostAddress ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun getAdditionalWifiInfo(): String {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return if (wifiInfo != null && wifiInfo.ssid != null) {
            "SSID: ${wifiInfo.ssid}, BSSID: ${wifiInfo.bssid}"
        } else {
            "Unknown Wi-Fi Details"
        }
    }
}