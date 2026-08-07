package com.example.placeslikee.presentation.profile.editMarker

import com.example.placeslikee.domain.models.UIMarker

sealed interface EditMarkerState {
    data object Idle: EditMarkerState
    data object Loading: EditMarkerState
    data class Error(val message: String): EditMarkerState
    data class Success(val marker: UIMarker): EditMarkerState
}
sealed interface EditMarkerEvent{
    data class Succeed(val message: String): EditMarkerEvent
    data class Error(val message: String): EditMarkerEvent
}

