package com.example.placeslikee.presentation.map.details

import com.example.placeslikee.domain.models.UIMarker

sealed interface DetailsState {
    data object Idle: DetailsState
    data object Loading: DetailsState
    data class Success(val marker: UIMarker?): DetailsState
}