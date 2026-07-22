package com.example.placeslikee.presentation.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.placeslikee.domain.models.NewMarkerIfo
import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.usecase.auth.GetCurrentIdUseCase
import com.example.placeslikee.domain.usecase.GetMapMarkUseCase
import com.example.placeslikee.domain.usecase.auth.IsUserLoggedInUseCase
import com.yandex.mapkit.map.CameraPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getMapMarkerUseCase: GetMapMarkUseCase,
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase,
    private val getCurrentIdUseCase: GetCurrentIdUseCase
) : ViewModel() {
    private val _mapState = MutableStateFlow(MapState())
    val mapState = _mapState.asStateFlow()

    //Auxiliary data for camera position change
    private val _isFirstTimeLoading = MutableStateFlow(true)
    val isFirstTimeLoading = _isFirstTimeLoading.asStateFlow()

    // Saving camera position
    private val _cameraPosition = MutableStateFlow<CameraPosition?>(null)

    //Auth navigation
    private val _navigateToAuth = MutableSharedFlow<Unit>()
    val navigateToAuth = _navigateToAuth.asSharedFlow()


    //Create marker navigation
    private val _navigateToCreateMarker = MutableSharedFlow<NewMarkerIfo>()
    val navigateToCreateMarker = _navigateToCreateMarker.asSharedFlow()

    fun updateCameraPosition(position: CameraPosition) {
        _cameraPosition.value = position

    }

    fun getLatestCameraPosition(): CameraPosition? = _cameraPosition.value
    fun setIsFirstTimeLoading(value: Boolean) {
        _isFirstTimeLoading.value = value
    }

    init {
        loadPoints()
    }

    private fun loadPoints() {
        viewModelScope.launch {
            _mapState.value = _mapState.value.copy(isLoading = true)
            getMapMarkerUseCase().collect { points ->
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
                handleLongClick(event.lat, event.lon)
            }

            is MapEvent.onPointClick -> {
                //if the creator -> ChangeMark
                //else -> MarkDiscription
                val clickMarker = mapState.value.points.find { it.id == event.pointId }
                _selectedMarker.value = clickMarker
            }
        }
    }

    fun dismissMarkerDetails() {
        _selectedMarker.value = null
    }

    private fun handleLongClick(lat: Double, lon: Double) {
        viewModelScope.launch {
            if (!isUserLoggedInUseCase()) {
                _navigateToAuth.emit(Unit)
            } else {
                val userId = getCurrentIdUseCase()
                if (userId != null) {
                    _navigateToCreateMarker.emit(NewMarkerIfo(lat, lon))
                }
            }
        }
    }
}