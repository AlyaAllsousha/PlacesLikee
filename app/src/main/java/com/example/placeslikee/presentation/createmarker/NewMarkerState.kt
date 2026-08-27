package com.example.placeslikee.presentation.createmarker

sealed interface NewMarkerState{
    data object Loading: NewMarkerState
    data object Idle: NewMarkerState
    data class Error(val message: String): NewMarkerState
}


