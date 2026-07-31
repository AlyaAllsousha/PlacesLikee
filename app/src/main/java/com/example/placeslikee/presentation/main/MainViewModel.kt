package com.example.placeslikee.presentation.main

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.placeslikee.data.local.entities.UserEntity
import com.example.placeslikee.domain.usecase.RefreshMarkersUseCase
import com.example.placeslikee.domain.usecase.auth.LogOutUseCase
import com.example.placeslikee.domain.usecase.auth.getCurrentUserUseCase
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
    private val refreshMarkersUseCase: RefreshMarkersUseCase

) : ViewModel() {


    val currentUser: StateFlow<UserEntity?> = getCurrentUserUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    //Update markers
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            logOutUseCase()
        }
    }
    fun refresh(){
        Log.d("my log", "refresh: started ")
        viewModelScope.launch {
            _isRefreshing.value = true
            try{
                refreshMarkersUseCase()
                delay(500)
            }
            finally {
                _isRefreshing.value = false
            }
        }
    }



}