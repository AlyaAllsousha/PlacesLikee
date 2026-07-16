package com.example.placeslikee.presentation.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.repositories.MapRepository
import com.example.placeslikee.domain.usecase.GetMapMarkUseCase
import com.yandex.mapkit.map.CameraPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getMapMarkerUseCase: GetMapMarkUseCase,
) : ViewModel() {
    private val _mapState = MutableStateFlow(MapState())
    val mapState = _mapState.asStateFlow()

    //auxiliary data for camera position change
    private val _isFirstTimeLoading = MutableStateFlow(true)
    val isFirstTimeLoading = _isFirstTimeLoading.asStateFlow()

    // Saving camera position
    private val _cameraPosition = MutableStateFlow<CameraPosition?>(null)

    fun updateCameraPosition(position: CameraPosition) {
        _cameraPosition.value = position

    }
    fun getLatestCameraPosition(): CameraPosition? = _cameraPosition.value
    fun setIsFirstTimeLoading(value: Boolean){
        _isFirstTimeLoading.value = value
    }
    init {
        loadPoints()
    }

    private fun loadPoints() {
        viewModelScope.launch {
            _mapState.value = _mapState.value.copy(isLoading = true)
            getMapMarkerUseCase.exec().collect { points ->
                _mapState.value = _mapState.value.copy(points = points, isLoading = false)
            }
        }
    }

    //Checking whether any marker is chosen
    private val _selectedMarker = MutableStateFlow<UIMarker?>(null)
    val selectedMarker = _selectedMarker.asStateFlow()


    fun onMapClick(event: MapEvent) {
        when (event) {
            is MapEvent.OnMapLongClick -> {
                //If not user -> SingIn
                //else -> createMark
                Log.d("my log", "onMapClick: coordinates: ${event.lat} ${event.lon}")
            }

            is MapEvent.onPointClick -> {
                //if the creator -> ChangeMark
                //else -> MarkDiscription
                val clickMarker = mapState.value.points.find { it.id == event.pointId}
                _selectedMarker.value = clickMarker
                Log.d("my log", "onMapClick: point click: ${event.pointId}")
            }
        }
    }

    fun dismissMarkerDetails(){
        _selectedMarker.value = null
    }
}