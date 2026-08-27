package com.example.placeslikee.presentation.map

import android.util.Log
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.query
import com.example.placeslikee.domain.models.NewMarkerIfo
import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.usecase.auth.GetCurrentIdUseCase
import com.example.placeslikee.domain.usecase.markermap.GetMapMarkUseCase
import com.example.placeslikee.domain.usecase.markermap.RefreshMarkersUseCase
import com.example.placeslikee.domain.usecase.auth.IsUserLoggedInUseCase
import com.yandex.mapkit.map.CameraPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getMapMarkerUseCase: GetMapMarkUseCase,
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase,
    private val getCurrentIdUseCase: GetCurrentIdUseCase,

    ) : ViewModel() {
    private val _mapState = MutableStateFlow(MapState())
    val mapState = _mapState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    private val _snackbarMessage = Channel<String>(Channel.CONFLATED)
    val snackbarMessage = _snackbarMessage.receiveAsFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    //Auxiliary data for camera position change
    private val _isFirstTimeLoading = MutableStateFlow(true)
    val isFirstTimeLoading = _isFirstTimeLoading.asStateFlow()

    // Saving camera position
    private val _cameraPosition = MutableStateFlow<CameraPosition?>(null)

    //Camera movements
    private val _cameraCommands = Channel<CameraCommand>(Channel.CONFLATED)
    val cameraCommands = _cameraCommands.receiveAsFlow()

    //Auth navigation
    private val _navigateToAuth = MutableSharedFlow<Unit>()
    val navigateToAuth = _navigateToAuth.asSharedFlow()

    //Checking whether any marker is chosen
    private val _selectedMarker = MutableStateFlow<UIMarker?>(null)
    val selectedMarker = _selectedMarker.asStateFlow()

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
            combine(
                getMapMarkerUseCase(),
                _searchQuery
            ) { points, query ->
                val filtered = if (query.isBlank()) {
                    points
                } else {
                    val lowerCaseQuery = query.lowercase()
                    points.filter { marker ->
                        marker.name.lowercase().contains(lowerCaseQuery) ||
                                (marker.authorName ?: "").lowercase().contains(lowerCaseQuery) ||
                                (lowerCaseQuery.startsWith("#") && (marker.description ?: "").lowercase().contains(lowerCaseQuery))
                    }

                }
                Pair(filtered, query)
            }
                .distinctUntilChanged()
                .collect { (filteredPoint, query) ->
                    _mapState.value = _mapState.value.copy(
                        points = filteredPoint,
                        isLoading = false
                    )
                if (filteredPoint.isEmpty() && query.isNotBlank()) {
                    _snackbarMessage.trySend("Ничего не найдено")
                } else {
                    _mapState.value =
                        _mapState.value.copy(points = filteredPoint, isLoading = false)
                    if (query.isNotBlank() && filteredPoint.isNotEmpty()) {
                        _cameraCommands.trySend(CameraCommand.FitBounds(filteredPoint))
                    }
                }
            }
        }
    }


    fun onMapClick(event: MapEvent) {
        when (event) {
            is MapEvent.OnMapLongClick -> {
                _cameraCommands.trySend(CameraCommand.MoveTo(event.lat, event.lon, 16.5f, false))
                handleLongClick(event.lat, event.lon)
            }

            is MapEvent.onPointClick -> {
                val clickMarker = mapState.value.points.find { it.id == event.pointId }
                if (clickMarker != null) {
                    _cameraCommands.trySend(
                        CameraCommand.MoveTo(
                            clickMarker.latitude,
                            clickMarker.longitude,
                            16.5f
                        )
                    )
                }
            }
        }
    }

    fun dismissMarkerDetails() {
        _selectedMarker.value = null
    }

    private fun handleLongClick(lat: Double, lon: Double) {
        viewModelScope.launch {
            _navigateToCreateMarker.emit(NewMarkerIfo(lat, lon))
        }
    }
}