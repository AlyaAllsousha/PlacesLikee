package com.example.placeslikee.presentation.map

import com.example.placeslikee.domain.models.UIMarker

sealed interface CameraCommand {
    data class MoveTo(
        val lat: Double,
        val lon: Double,
        val zoom: Float,
        val animate: Boolean = true) : CameraCommand
    data class FitBounds(val points: List<UIMarker>) : CameraCommand
}