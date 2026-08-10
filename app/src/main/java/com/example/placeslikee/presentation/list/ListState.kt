package com.example.placeslikee.presentation.list

import com.example.placeslikee.domain.models.UIMarker

sealed interface ListState {
    data object Loading: ListState
    data class Success(val markers: List<UIMarker>): ListState
    data class Error (val message: String): ListState
}