package com.example.placeslikee.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.usecase.likes.ToggleLikedUseCase
import com.example.placeslikee.domain.usecase.markermap.GetMapMarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getMapMarkUseCase: GetMapMarkUseCase,
    private val toggleLikedUseCase: ToggleLikedUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<ListState>(ListState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }


    init {
        loadPoints()
    }

    private fun loadPoints() {
        viewModelScope.launch {
            combine(
                getMapMarkUseCase(),
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
            }.collect { (filteredPoint, query) ->
                if (filteredPoint.isEmpty() && query.isNotBlank()) {
                    _uiState.value = ListState.Error("Ничего не найдено")
                }
                _uiState.value = ListState.Success(filteredPoint)

            }
        }
    }



    fun onToggleLike(markerId: String) {
        viewModelScope.launch {
            toggleLikedUseCase(markerId)
        }
    }

}