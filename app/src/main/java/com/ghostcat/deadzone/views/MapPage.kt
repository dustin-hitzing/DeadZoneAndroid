package com.ghostcat.deadzone.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ghostcat.deadzone.models.MapPoint
import com.ghostcat.deadzone.viewmodels.MapViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapPage(navController: NavController, modifier: Modifier, viewModel: MapViewModel) {
    val selectedReport = viewModel.selectedTestReport.collectAsStateWithLifecycle().value
    val points = selectedReport?.testResults?.map {
        val ll = LatLng(it.geoLocation?.latitude ?: 0.0, it.geoLocation?.longitude ?: 0.0)
        MapPoint(
            latlng = ll,
            connected = it.connectionInfo.isConnected && it.connectionInfo.hasService
        )
    } ?: emptyList()

    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(points) {
        if (points.isNotEmpty()) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(points.first().latlng, 18f)
        }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        points.forEach {
            val markerIcon = if (it.connected) {
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            } else {
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            }

            Marker(
                state = MarkerState(position = it.latlng),
                icon = markerIcon,
                title = "Connectivity: ${if (it.connected) "Connected" else "Disconnected"}"
            )
        }

        if (points.size > 1) {
            com.google.maps.android.compose.Polyline(
                points = points.map { it.latlng },
                color = Color.Blue,
                width = 5f
            )
        }
    }
}