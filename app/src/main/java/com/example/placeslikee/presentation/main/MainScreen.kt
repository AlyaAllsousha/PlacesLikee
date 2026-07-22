package com.example.placeslikee.presentation.main

import android.R.attr.title
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.placeslikee.presentation.map.MapScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.placeslikee.domain.models.NewMarkerIfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCreateMarker: (NewMarkerIfo) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()
    var isMapVisible by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    fun navigateSafely(action: () -> Unit) {
        scope.launch {
            isMapVisible = false
            delay(50)
            action()
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,

            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(
                        enabled = user != null,
                        onClick = {
                            navigateSafely { onNavigateToProfile() }
                        }
                    )
                ) {
                    if (user != null) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Профиль",
                            modifier = Modifier.size(30.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = user?.name ?: "",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Button(
                    onClick = {
                        if (user != null) {
                            viewModel.logout()
                        } else {
                            navigateSafely { onNavigateToAuth() }
                        }
                    }
                ) {
                    Text(text = if (user != null) "Выйти" else "Войти")
                }
            }
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    label = { Text(text = "Поиск меток") },
                    placeholder = { Text(text = "Введите текст или автора") },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = { },
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
            Spacer(Modifier.height(16.dp))
            if (isMapVisible) {
                MapScreen(
                    onNavigateToAuth = {
                        navigateSafely { onNavigateToAuth() }
                    },
                    onNavigateToCreateMarker = {
                        navigateSafely { onNavigateToCreateMarker(it) }
                    }
                )
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isMapVisible = true
            }
            else if(event == Lifecycle.Event.ON_PAUSE){
                isMapVisible = false
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

}