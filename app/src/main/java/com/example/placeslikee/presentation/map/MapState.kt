package com.example.placeslikee.presentation.map

import com.example.placeslikee.domain.models.MapPoint

data class MapState(
    val points: List<MapPoint> = emptyList(),
    val isLoading: Boolean = false,
)
