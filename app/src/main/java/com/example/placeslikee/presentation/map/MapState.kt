package com.example.placeslikee.presentation.map

import com.example.placeslikee.domain.models.UIMarker

data class MapState(
    val points: List<UIMarker> = emptyList(),
    val isLoading: Boolean = false,
)
