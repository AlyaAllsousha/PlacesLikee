package com.example.placeslikee.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.placeslikee.domain.usecase.likes.ToggleLikedUseCase
import com.example.placeslikee.domain.usecase.markermap.GetMapMarkUseCase
import com.example.placeslikee.domain.usecase.markermap.ObserveFilteredMarkersUseCase
import com.example.placeslikee.domain.models.extensions.FilterSortState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getMapMarkUseCase: GetMapMarkUseCase,
    private val toggleLikedUseCase: ToggleLikedUseCase,
    private val observeFilteredMarkersUseCase: ObserveFilteredMarkersUseCase
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _filterSortState = MutableStateFlow(FilterSortState())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterSortState(state: FilterSortState) {
        _filterSortState.value = state
    }

    val uiState: StateFlow<ListState> = observeFilteredMarkersUseCase(
        sourceFlow = getMapMarkUseCase(),
        queryFlow = _searchQuery,
        filterStateFlow = _filterSortState
    ).map { filteredList ->
        if (filteredList.isEmpty() && _searchQuery.value.isNotBlank()) {
            ListState.Error("Ничего не найдено")
        } else {
            ListState.Success(filteredList)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ListState.Loading
    )


    fun onToggleLike(markerId: String) {
        viewModelScope.launch {
            toggleLikedUseCase(markerId)
        }
    }

}