package com.example.placeslikee.presentation.createmarker

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.placeslikee.domain.usecase.markermap.CreateMarkerUseCase
import com.example.placeslikee.domain.usecase.auth.GetCurrentIdUseCase
import com.example.placeslikee.domain.usecase.images.SaveImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateMarkerViewModel @Inject constructor(
    private val getCurrentIdUseCase: GetCurrentIdUseCase,
    private val createMarkerUseCase: CreateMarkerUseCase,
    private val saveImageUseCase: SaveImageUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val lat: Double = savedStateHandle.get<String>("lat")?.toDoubleOrNull() ?: 0.0
    val lon: Double = savedStateHandle.get<String>("lon")?.toDoubleOrNull() ?: 0.0

    private val _state = MutableStateFlow<NewMarkerState>(NewMarkerState.Idle)
    val state = _state.asStateFlow()

    private val _navigateBack = MutableSharedFlow<Unit>()
    val navigateBack = _navigateBack.asSharedFlow()

    fun onSaveClick(name: String, description: String?, image: String?) {
        viewModelScope.launch {
            _state.value = NewMarkerState.Loading
            try {
                val userId = getCurrentIdUseCase()
                val localImagePath = if (image != null) {
                    saveImageUseCase.invoke(image)
                } else {
                    null
                }
                createMarkerUseCase(
                    lat = lat,
                    lon = lon,
                    authorId = userId,
                    name = name,
                    description = description,
                    image = localImagePath,
                )
                _navigateBack.emit(Unit)
            } catch (e: Exception) {
                _state.value = NewMarkerState.Error(e.localizedMessage ?: "Неизвестная ошибка")
            }
        }

    }

    fun consumeError() {
        _state.value = NewMarkerState.Idle
    }

}