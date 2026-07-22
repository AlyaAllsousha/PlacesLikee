package com.example.placeslikee.presentation.newmarker

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.placeslikee.domain.usecase.CreateMarkerUseCase
import com.example.placeslikee.domain.usecase.auth.GetCurrentIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateMarkerViewModel @Inject constructor(
    private val getCurrentIdUseCase: GetCurrentIdUseCase,
    private val createMarkerUseCase: CreateMarkerUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel(){
    val lat: Double = savedStateHandle.get<String>("lat")?.toDoubleOrNull() ?: 0.0
    val lon: Double = savedStateHandle.get<String>("lon")?.toDoubleOrNull() ?: 0.0

    private val _state =MutableStateFlow<NewMarkerState>(NewMarkerState.Idle)
    val state = _state.asStateFlow()

    private val _navigateBack = MutableSharedFlow<Unit>()
    val navigateBack = _navigateBack.asSharedFlow()

    fun onSaveClick(name: String, description: String?, image: String?){
        viewModelScope.launch {
            _state.value = NewMarkerState.Loading
            try{
                val userId = getCurrentIdUseCase()
                if(userId != null){
                    createMarkerUseCase(
                        lat = lat,
                        lon = lon,
                        authorId = userId,
                        name = name,
                        description = description,
                        image = image,
                    )
                    _navigateBack.emit(Unit)
                }
                else{
                    _state.value  = NewMarkerState.Error("Для создания записи необходимо авторизоваться")
                }
            }
            catch (e: Exception){
                _state.value = NewMarkerState.Error(e.localizedMessage ?: "Неизвестная ошибка")
            }
        }

    }
    fun consumeError(){
        _state.value = NewMarkerState.Idle
    }

}