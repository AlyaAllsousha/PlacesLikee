package com.example.placeslikee.presentation.map

sealed class MapEvent {
    data class OnMapLongClick(val lat: Double, val lon: Double): MapEvent()
    data class onPointClick(val pointId: String) : MapEvent()
}