package com.example.placeslikee.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.placeslikee.domain.usecase.profile.GetUsersMarkerUseCase
import com.example.placeslikee.domain.usecase.auth.IsUserLoggedInUseCase
import com.example.placeslikee.domain.usecase.auth.getCurrentUserUseCase
import com.example.placeslikee.domain.usecase.profile.ChangeUserEmailUseCase
import com.example.placeslikee.domain.usecase.profile.DeleteMarkerUseCase
import com.example.placeslikee.domain.usecase.profile.SyncAuthDataUseCase
import com.example.placeslikee.domain.usecase.profile.changeUserNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUsersMarkerUseCase: GetUsersMarkerUseCase,
    private val changeUserNameUseCase: changeUserNameUseCase,
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase,
    private val getCurrentUserUseCase: getCurrentUserUseCase,
    private val deleteMarkerUseCase: DeleteMarkerUseCase,
    private val changeUserEmailUseCase: ChangeUserEmailUseCase,
    private val syncAuthDataUseCase: SyncAuthDataUseCase
): ViewModel() {
    private val _state = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<ProfileUiEvent>()
    val uiEvent  = _uiEvent.receiveAsFlow()


    private val _isEmailChanging = MutableStateFlow(false)
    val isEmailChanging  = _isEmailChanging.asStateFlow()

    init{
        loadMarkers()
        viewModelScope.launch {
            syncAuthDataUseCase()
        }
    }
    private fun loadMarkers(){
        if(isUserLoggedInUseCase()) {
            viewModelScope.launch {
                _state.value = ProfileState.Loading
                combine(
                    getCurrentUserUseCase(),
                    getUsersMarkerUseCase()
                ) { user, markers ->
                    if (user == null) {
                        ProfileState.Loading
                    } else {
                        ProfileState.Success(
                            markersList = markers,
                            user = user
                        )
                    }
                }.collect{combinedState ->
                    _state.value = combinedState

                }

            }
        }
        else{
            _state.value = ProfileState.Unauthorized
        }
    }
    fun onChangeUserInfo(name: String) {
        viewModelScope.launch {
            changeUserNameUseCase(name)
            _uiEvent.send(ProfileUiEvent.NameChanged("Имя успешно изменено"))
        }
    }
    fun onChangeEmail(email: String, password: String){
        viewModelScope.launch {
            try {
                _isEmailChanging.value = true
                val result = changeUserEmailUseCase(email, password)
                _uiEvent.send(ProfileUiEvent.EmailChangeEmailSent(result))
            }
            finally {
                _isEmailChanging.value = false
            }

        }
    }
    fun  deleteMarker(id: String){
        viewModelScope.launch {
            deleteMarkerUseCase(id)
        }
    }
    fun mapErrorToMessage(errorMsg: String?): String {
        Log.d("my log", "mapErrorToMessage: $errorMsg")
        when {
            (errorMsg == null) -> {
                return "Не удалось изменить почту"
            }

            ( errorMsg.contains("credential is incorrect", ignoreCase = true)) -> {
                return "Неверный пароль"
            }

            (errorMsg.contains("network", ignoreCase = true)) -> {
                return "Нет подключения к интернету"
            }

            (errorMsg.contains("email address is already", ignoreCase = true)) ->{
                return "Этот email уже используется"
            }
            (errorMsg.contains("email", ignoreCase = true) || errorMsg.contains(
                "invalid",
                ignoreCase = true
            )) -> {
                return "Неверный формат почты"
            }

            else -> {
                return errorMsg
            }

        }
    }
}