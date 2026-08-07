package com.example.placeslikee.presentation.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.usecase.auth.IsUserLoggedInUseCase
import com.example.placeslikee.domain.usecase.likes.GetLikedMarksUseCase
import com.example.placeslikee.domain.usecase.likes.ToggleLikedUseCase
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
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow<FavouriteState>(FavouriteState.Idle)
    val uiState = _uiState.asStateFlow()

    //Checking whether any marker is chosen
    private val _selectedMarker = MutableStateFlow<UIMarker?>(null)
    val selectedMarker = _selectedMarker.asStateFlow()

    private val _inputQuery = MutableStateFlow("")
    val inputQuery = _inputQuery.asStateFlow()

    private val _appliedQuery = MutableStateFlow("")
    val appliedQuery = _appliedQuery.asStateFlow()


    val searchResults = combine(
        getLikedMarksUseCase(),
        _inputQuery
    ) { markers, query ->
        if (query.isBlank()) {
            emptyList()
        } else {
            val lowerQuery = query.lowercase()
            markers.filter {
                it.name.lowercase().contains(lowerQuery, ignoreCase = true) ||
                        (it.authorName ?: "Неизвестный").lowercase().contains(lowerQuery, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init{
        loadLikedMarks()
    }

    private fun loadLikedMarks() {
        if (isUserLoggedInUseCase()) {
            viewModelScope.launch {
                _uiState.value = FavouriteState.Loading
                combine(
                    getLikedMarksUseCase(),
                    _appliedQuery
                ){marks, query ->
                    if(query.isBlank()){
                        marks
                    }
                    else{
                        val lowerQuery= query.lowercase()
                        marks.filter{
                            it.name.lowercase().contains(lowerQuery) ||
                                    (it.authorName ?: "").lowercase().contains(lowerQuery)
                        }
                    }
                }.collect { filteredMarks ->
                    _uiState.value = FavouriteState.Success(filteredMarks)
                }
            }
        }
        else{
            _uiState.value = FavouriteState.Unauthorized
        }
    }
    fun onMarkerClick(id: String){
        if(uiState.value is FavouriteState.Success){
            val marker = (uiState.value as FavouriteState.Success).markers.find { it.id == id }
            _selectedMarker.value = marker
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

    fun dismissMarkerDetails() {
        _selectedMarker.value = null
    }

}