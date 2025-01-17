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
        val hasService = hasService(networkType)
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
            wifiInfo = wifiInfo,
            hasService = hasService
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

    private fun hasService(networkType: String): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
            ?: return false

        // The most common check: if the system says it's validated,
        // the network is actually online (beyond just "available").
        val isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        if (!isValidated) {
            return false
        }

        // Optional: If it's Cellular, you might also check TelephonyManager signal strength:
        if (networkType == "Cellular") {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            // For example, you'd check telephonyManager.signalStrength or ServiceState.
            // This is more advanced, requiring extra permissions + logic.
            // We'll keep it simple here and assume validated means "has service."
        }

        return true
    }
}