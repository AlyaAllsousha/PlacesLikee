package com.example.placeslikee.presentation.profile.editMarker

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.usecase.markermap.GetMarkerByIdUseCase
import com.example.placeslikee.domain.usecase.profile.EditMarkerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditMarkerViewModel @Inject constructor(
    private val getMarkerByIdUseCase: GetMarkerByIdUseCase,
    private val editMarkerUseCase: EditMarkerUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val markerId = savedStateHandle.get<String>("markerId") ?: "0"

    private val _editMarkerState = MutableStateFlow<EditMarkerState>(EditMarkerState.Idle)
    val editMarkerState = _editMarkerState.asStateFlow()

    private val _uiEvent = Channel<EditMarkerEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _markerIsUpdating = MutableStateFlow(false)
    val markerIsUpdating = _markerIsUpdating.asStateFlow()


    init {
        viewModelScope.launch {
            _editMarkerState.value = EditMarkerState.Loading
            getMarkerByIdUseCase(markerId).collect { marker ->
                if (marker == null) {
                    _editMarkerState.value = EditMarkerState.Error("Маркер не найден")
                } else {
                    _editMarkerState.value = EditMarkerState.Success(marker)
                }
            }
        }
    }

    fun onSubmit(
        lat: Double,
        longitude: Double,
        name: String,
        description: String?,
        image: String?,
    ) {

        if (_editMarkerState.value !is EditMarkerState.Success)
            return

        val originMarker = (_editMarkerState.value as EditMarkerState.Success).marker
        val updatedMarker = originMarker.copy(
            latitude = lat,
            longitude = longitude,
            name = name,
            description = description,
            image = image,
            
            uiTimestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            _markerIsUpdating.value = true
            val result = editMarkerUseCase(updatedMarker)
            result.onSuccess {
                _uiEvent.send(EditMarkerEvent.Succeed("Место изменено"))
            }
            result.onFailure { exception ->
                _uiEvent.send(
                    EditMarkerEvent.Error(
                        exception.message ?: "Не удалось сохранить изменения"
                    )
                )
            }
        }
        _markerIsUpdating.value = false

    }
    fun reloadMarker (){
        viewModelScope.launch {
            _editMarkerState.value = EditMarkerState.Loading
            getMarkerByIdUseCase(markerId).collect { marker ->
                if (marker == null) {
                    _editMarkerState.value = EditMarkerState.Error("Маркер не найден")
                } else {
                    _editMarkerState.value = EditMarkerState.Success(marker)
                }
            }
        }
    }
}