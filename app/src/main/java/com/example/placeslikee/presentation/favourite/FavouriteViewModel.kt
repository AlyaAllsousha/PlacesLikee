package com.example.placeslikee.presentation.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.models.extensions.FilterSortState
import com.example.placeslikee.domain.models.extensions.SortOption
import com.example.placeslikee.domain.usecase.auth.IsUserLoggedInUseCase
import com.example.placeslikee.domain.usecase.likes.GetLikedMarksUseCase
import com.example.placeslikee.domain.usecase.likes.ToggleLikedUseCase
import com.example.placeslikee.domain.usecase.markermap.ObserveFilteredMarkersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val getLikedMarksUseCase: GetLikedMarksUseCase,
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase,
    private val toggleLikedUseCase: ToggleLikedUseCase,
    private val observeFilteredMarkersUseCase: ObserveFilteredMarkersUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<FavouriteState>(FavouriteState.Idle)
    val uiState = _uiState.asStateFlow()

    //Defence from liking spam
    private var isLiking = false
    private var lastClickTime = 0L

    private val _inputQuery = MutableStateFlow("")
    val inputQuery = _inputQuery.asStateFlow()

    private val _appliedQuery = MutableStateFlow("")
    val appliedQuery = _appliedQuery.asStateFlow()

    private val _filterState = MutableStateFlow(FilterSortState())


    val searchResults = observeFilteredMarkersUseCase(
        sourceFlow = getLikedMarksUseCase(),
        queryFlow = _inputQuery,
        filterStateFlow = _filterState
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadLikedMarks()
    }

    private fun loadLikedMarks() {
        if (!isUserLoggedInUseCase()) {
            _uiState.value = FavouriteState.Unauthorized
            return
        }
        viewModelScope.launch {
            _uiState.value = FavouriteState.Loading
            observeFilteredMarkersUseCase(
                sourceFlow = getLikedMarksUseCase(),
                queryFlow = _appliedQuery,
                filterStateFlow = _filterState
            ).collect { filteredMarks ->
                _uiState.value = FavouriteState.Success(filteredMarks)
            }
        }

    }

    fun updateInputQuery(query: String) {
        _inputQuery.value = query
        if (query.isEmpty()) {
            _appliedQuery.value = ""
        }
    }

    fun selectPlace(title: String) {
        _inputQuery.value = title
        _appliedQuery.value = title
    }

    fun applySearch() {
        _appliedQuery.value = _inputQuery.value
    }

    fun onToggleLike(markerId: String) {
        val currentTime = System.currentTimeMillis()
        if (isLiking || currentTime - lastClickTime < 500) return
        isLiking = true
        lastClickTime = currentTime
        viewModelScope.launch {
            try {
                toggleLikedUseCase(markerId)
            } finally {
                isLiking = false
            }
        }
    }


}