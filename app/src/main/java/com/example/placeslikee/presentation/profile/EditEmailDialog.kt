package com.example.placeslikee.presentation.profile

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun EditEmailDialog(
    currentEmail: String,
    onDismiss: () -> Unit,
    isLoading: Boolean,
    serverError: String?,
    onClearError: ()-> Unit,
    onConfirm: (newEmail: String, password: String) -> Unit
) {
    var newEmail by remember { mutableStateOf(currentEmail) }
    var password by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val isEmailChanged = newEmail.trim() != currentEmail
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(newEmail.trim()).matches()
    val isPasswordValid = password.length >= 6

    val showEmailError = newEmail.isNotEmpty() && !isEmailValid
    val showPasswordError = (password.isNotEmpty() && !isPasswordValid) || serverError != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Изменить Email") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Для изменения почты в целях безопасности необходимо ввести текущий пароль.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )

                OutlinedTextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    label = { Text("Новый Email") },
                    singleLine = true,
                    isError = showEmailError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), // Клавиатура с собачкой @
                    supportingText = {
                        if (showEmailError) Text("Введите корректный email адрес")
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (serverError != null) onClearError()},
                    label = { Text("Текущий пароль") },
                    singleLine = true,
                    isError = showPasswordError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    enabled = !isLoading,
                    supportingText = {
                        if (serverError != null) {
                            Text(serverError, color = MaterialTheme.colorScheme.error)
                        }
                        else if (password.isNotEmpty() && !isPasswordValid) {
                            Text("Пароль должен содержать минимум 6 символов")
                        }                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onConfirm(newEmail.trim(), password)
                },
                enabled = isEmailChanged && isEmailValid && isPasswordValid && !isLoading
            ) {
                if (isLoading) {
                    Text("Загрузка")
                    Spacer(Modifier.width(8.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Сохранить")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Отмена")
            }
        }
    )
}