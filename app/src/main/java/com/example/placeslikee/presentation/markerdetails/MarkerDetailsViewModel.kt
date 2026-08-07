package com.example.placeslikee.presentation.markerdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.placeslikee.domain.usecase.likes.ToggleLikedUseCase
import com.example.placeslikee.domain.usecase.markermap.GetMarkerByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@HiltViewModel(
    assistedFactory = MarkerDetailsViewModel.Factory::class
)
class MarkerDetailsViewModel @AssistedInject constructor(
    @Assisted private val markerId: String,
    private val toggleLikedUseCase: ToggleLikedUseCase,
    private val getMarkerByIdUseCase: GetMarkerByIdUseCase
): ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(markerId: String): MarkerDetailsViewModel
    }

    private val _markerDetails = MutableStateFlow<DetailsState>(DetailsState.Idle)
    val markerDetails = _markerDetails.asStateFlow()

    init{
        viewModelScope.launch {
            _markerDetails.value = DetailsState.Loading
            getMarkerByIdUseCase(markerId).collect{details ->
                _markerDetails.value = DetailsState.Success(details)
            }
        }
    }
    fun onToggleLike(){
        viewModelScope.launch {
            toggleLikedUseCase(markerId)
        }
    }
}