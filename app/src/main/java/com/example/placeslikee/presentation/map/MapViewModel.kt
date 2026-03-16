package com.example.placeslikee.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.placeslikee.domain.repositories.MapRepository
import com.example.placeslikee.domain.usecase.GetMapMarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getMapMarkerUseCase : GetMapMarkUseCase
) : ViewModel() {
    private val _mapState = MutableStateFlow(MapState())
    val mapState = _mapState.asStateFlow()
    init {
        loadPoints()
    }

    private fun loadPoints(){
        viewModelScope.launch {
            getMapMarkerUseCase.exec().collect{ points ->
                _mapState.value = _mapState.value.copy(points = points)

            }
        }
    }
    fun onMapClick(event: MapEvent){
        when(event){
            MapEvent.OnMapLoaded -> {
                _mapState.value = _mapState.value.copy(isLoading = true)
            }
            is MapEvent.OnMapLongClick -> {
                //If not user -> SingIn
                //else -> createMark
            }
            is MapEvent.onPointClick -> {
                //if the creator -> ChangeMark
                //else -> MarkDiscription
            }
        }
    }
}