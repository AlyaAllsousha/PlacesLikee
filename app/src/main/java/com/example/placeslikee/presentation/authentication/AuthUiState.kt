package com.example.placeslikee.presentation.authentication

//Process state
sealed interface AuthUiState {
    data object Idle: AuthUiState
    data object Loading: AuthUiState
    data class Error(val message: String) : AuthUiState
    data object Success : AuthUiState
}

// Screen state
data class AuthScreenState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val isLoginMode: Boolean = true,
    val status: AuthUiState = AuthUiState.Idle
)
