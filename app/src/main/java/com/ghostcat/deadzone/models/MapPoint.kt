package com.ghostcat.deadzone.models

import com.google.android.gms.maps.model.LatLng

data class MapPoint(
    val latlng: LatLng,
    val connected: Boolean,
)
