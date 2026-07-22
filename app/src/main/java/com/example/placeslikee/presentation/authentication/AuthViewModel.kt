package com.example.placeslikee.presentation.authentication

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.placeslikee.domain.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel(){

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val _state = MutableStateFlow(AuthScreenState())
    val state = _state.asStateFlow()

    fun onEmailChange(value: String) = _state.update { it.copy(email = value, status = AuthUiState.Idle) }

    fun onPasswordChanged(value: String) = _state.update{ it.copy(password = value, status = AuthUiState.Idle)}

    fun onNameChanged(value: String) = _state.update{ it.copy(name = value)}

    fun toggleMode() = _state.update{ it.copy(isLoginMode = !it.isLoginMode, status = AuthUiState.Idle)}

    fun authenticate(){
        val currentState = _state.value

        if(!validate(currentState)) return

        viewModelScope.launch {
            _state.update { it.copy(status = AuthUiState.Loading) }

            val result = if (currentState.isLoginMode){
                authRepository.login(currentState.email, currentState.password)
            } else{
                authRepository.register(currentState.email, currentState.password, currentState.name)
            }
            result.onSuccess {
                _state.update { it.copy(status = AuthUiState.Success) }
            }
                .onFailure { error ->
                    _state.update { it.copy(status = AuthUiState.Error(mapErrorToMessage(error))) }
                }
        }
    }

    private fun validate(s: AuthScreenState): Boolean{
        return when{
            s.email.isBlank() || s.password.isBlank() -> {
                _state.update { it.copy(status = AuthUiState.Error("Заполните все поля")) }
                false
            }
            !EMAIL_REGEX.matches(s.email.trim()) -> {
                _state.update { it.copy(status = AuthUiState.Error("Некорректный формат email")) }
                false
            }
            !s.isLoginMode && s.name.isBlank() -> {
                _state.update { it.copy(status = AuthUiState.Error("Введите имя")) }
                false
            }
            s.password.length < 6 ->{
                _state.update { it.copy(status = AuthUiState.Error("Пароль должен содержать минимум 6 символов")) }
                false
            }
            else -> true
        }
    }
    private fun mapErrorToMessage(e: Throwable): String{
        return when {
            e.message?.contains("password") == true -> "Неверный пароль"
            e.message?.contains("email") == true -> "Неверный Email"
            e.message?.contains("network") == true -> "Нет интернета"
            e.message?.contains("credential is incorrect") == true -> "Неверные почта или пароль"
            else -> "Ошибка: ${e.localizedMessage}"
         }
    }
}