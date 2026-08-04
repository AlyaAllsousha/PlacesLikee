package com.example.placeslikee.presentation.favourite

import com.example.placeslikee.domain.models.UIMarker

sealed interface FavouriteState {
    data object Idle: FavouriteState
    data object Loading: FavouriteState
    data object Unauthorized: FavouriteState
    data class Success(val markers: List<UIMarker>): FavouriteState
}