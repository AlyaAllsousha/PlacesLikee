package com.example.placeslikee.presentation.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation


@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToMap: () -> Unit
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.status) {
        if (state.status is AuthUiState.Success) {
            onNavigateToMap()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp, 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (state.isLoginMode) " Вход" else "Регистрация",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (!state.isLoginMode) {
            AuthTextField(
                value = state.name,
                label = "Ваше имя",
                onValueChange = viewModel::onNameChanged
            )
        }
        AuthTextField(
            value = state.email,
            label = "Email",
            onValueChange = viewModel::onEmailChange
        )
        AuthTextField(
            value = state.password,
            label = "Пароль",
            isPassword = true,
            onValueChange = viewModel::onPasswordChanged
        )

        if(state.status is AuthUiState.Error){
            Text(
                text = (state.status as AuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = viewModel::authenticate,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = state.status !is AuthUiState.Loading
        ){
            if(state.status is AuthUiState.Loading){
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            }
            else{
                Text(if (state.isLoginMode) "Войти" else "Зарегистрироваться")
            }
        }
        TextButton(onClick = viewModel::toggleMode) {
            Text( if (state.isLoginMode) "Еще нет аккаунта? Регистрация" else "Уже есть аккаунт? Войти")
        }
    }
}

@Composable
fun AuthTextField(
    value: String,
    label: String,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true
    )
}
