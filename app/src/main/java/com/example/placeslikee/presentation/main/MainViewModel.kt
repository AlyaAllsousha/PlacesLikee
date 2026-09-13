package com.example.placeslikee.presentation.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.placeslikee.data.local.entities.UserEntity
import com.example.placeslikee.domain.usecase.markermap.RefreshMarkersUseCase
import com.example.placeslikee.domain.usecase.auth.LogOutUseCase
import com.example.placeslikee.domain.usecase.auth.getCurrentUserUseCase
import com.example.placeslikee.domain.usecase.markermap.GetMapMarkUseCase
import com.example.placeslikee.domain.usecase.markermap.ObserveFilteredMarkersUseCase
import com.example.placeslikee.domain.models.extensions.FilterSortState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentUserUseCase: getCurrentUserUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val refreshMarkersUseCase: RefreshMarkersUseCase,
    private val getMapMarkerUseCase: GetMapMarkUseCase,
    private val observeFilteredMarkersUseCase: ObserveFilteredMarkersUseCase

) : ViewModel() {

    //For immediate show of dropdown list
    private val _inputQuery = MutableStateFlow("")
    val inputQuery = _inputQuery.asStateFlow()

    private val _filterSortSate = MutableStateFlow(FilterSortState())
    val filterSortState = _filterSortSate.asStateFlow()

    fun updateFilterSortState(newState: FilterSortState) {
        _filterSortSate.value = newState
    }
    //For show appropriate markers
    private val _appliedQuery = MutableStateFlow("")
    val appliedQuery = _appliedQuery.asStateFlow()

    private val _isMapView = MutableStateFlow(true)
    val isMapView = _isMapView.asStateFlow()


    val searchResults = observeFilteredMarkersUseCase(
        sourceFlow = getMapMarkerUseCase(),
        queryFlow = _inputQuery,
        filterStateFlow = _filterSortSate
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList())

    val currentUser: StateFlow<UserEntity?> = getCurrentUserUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun updateInputQuery(query: String) {
        _inputQuery.value = query
        if (query.isEmpty()) {
            _appliedQuery.value = ""
        }
    }



    fun applySearch() {
        _appliedQuery.value = _inputQuery.value
    }

    fun selectPlace(title: String) {
        _inputQuery.value = title
        _appliedQuery.value = title
    }

    fun toggleIsMap() {
        _isMapView.value = !_isMapView.value
    }

    fun logout() {
        viewModelScope.launch {
            logOutUseCase()
        }
    }

    fun refresh() {
        Log.d("my log", "refresh: started ")
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                refreshMarkersUseCase()
                delay(500)
            } finally {
                _isRefreshing.value = false
            }
        }
    }


}