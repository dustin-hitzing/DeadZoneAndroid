package com.ghostcat.deadzone.models

import kotlinx.serialization.Serializable

@Serializable
data class ConnectionInfo(
    val isConnected: Boolean,
    val networkType: String,
    val ipAddress: String,
    val providerName: String,
    val wifiInfo: String
)
