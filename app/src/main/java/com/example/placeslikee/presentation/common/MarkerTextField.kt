package com.example.placeslikee.presentation.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun MarkerTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: Painter? = null,
    supportingText: String? = null,
    singleLine: Boolean,
    isError: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = 3,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
            )
        },
        leadingIcon = leadingIcon?.let { icon ->
            {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = if (value.isNotBlank() && !isError)
                        MaterialTheme.colorScheme.primary
                    else if (isError)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.outline
                )
            }
        },
        singleLine = singleLine,
        minLines = minLines,
        supportingText = if(supportingText != null && isError) {
            {Text(supportingText)}
        } else null,
        maxLines = maxLines,
        isError = isError,
        keyboardOptions = keyboardOptions,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}