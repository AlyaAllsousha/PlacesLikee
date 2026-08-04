package com.example.placeslikee.presentation.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.placeslikee.domain.usecase.auth.IsUserLoggedInUseCase
import com.example.placeslikee.domain.usecase.likes.GetLikedMarksUseCase
import com.example.placeslikee.domain.usecase.likes.ToggleLikedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val getLikedMarksUseCase: GetLikedMarksUseCase,
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow<FavouriteState>(FavouriteState.Idle)
    val uiState = _uiState.asStateFlow()

    init{
        loadLikedMarks()
    }
    private fun loadLikedMarks() {
        if (isUserLoggedInUseCase()) {
            viewModelScope.launch {
                _uiState.value = FavouriteState.Loading
                getLikedMarksUseCase().collect { marks ->
                    _uiState.value = FavouriteState.Success(marks)
                }
            }
        }
        else{
            _uiState.value = FavouriteState.Unauthorized
        }
    }
}