package com.example.placeslikee.presentation

import android.widget.EditText
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.placeslikee.presentation.map.MapScreen

@Composable
fun MainScreen() {
    Column (
        modifier = Modifier.fillMaxSize().padding(32.dp, 64.dp),
    ){
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ){
            TextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth().weight(1f),
                label = { Text(text = "Поиск меток") },
                placeholder = {Text(text = "Введите текст или автора")},
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {  },
                        enabled = true
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Найти",
                        )
                    }
                },
            )

        }
        Spacer(Modifier.height(10.dp))
        MapScreen()
    }
}