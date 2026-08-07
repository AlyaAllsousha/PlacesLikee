package com.example.placeslikee.presentation.main

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.placeslikee.data.local.entities.UserEntity
import com.example.placeslikee.domain.usecase.markermap.RefreshMarkersUseCase
import com.example.placeslikee.domain.usecase.auth.LogOutUseCase
import com.example.placeslikee.domain.usecase.auth.getCurrentUserUseCase
import com.example.placeslikee.domain.usecase.markermap.GetMapMarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentUserUseCase: getCurrentUserUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val refreshMarkersUseCase: RefreshMarkersUseCase,
    private val getMapMarkerUseCase: GetMapMarkUseCase

) : ViewModel() {

    //For immediate show of dropdown list
    private val _inputQuery = MutableStateFlow("")
    val inputQuery = _inputQuery.asStateFlow()

    //For show appropriate markers
    private val _appliedQuery = MutableStateFlow("")
    val appliedQuery = _appliedQuery.asStateFlow()

    val searchResults = combine(
        getMapMarkerUseCase(),
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