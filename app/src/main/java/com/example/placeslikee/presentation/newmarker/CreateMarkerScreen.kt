package com.example.placeslikee.presentation.newmarker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMarkerScreen(
    viewModel: CreateMarkerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var image by rememberSaveable() { mutableStateOf("") }

    val snackBarHostState = remember { SnackbarHostState() }

    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect {
            onNavigateBack()
        }
    }

    LaunchedEffect(state) {
        if (state is NewMarkerState.Error) {
            val errorMessage = (state as NewMarkerState.Error).message
            snackBarHostState.showSnackbar(message = errorMessage)
            viewModel.consumeError()
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Новое место",
                        style = MaterialTheme.typography.titleLarge
                    )

            }
        }
    ) {paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .imePadding()
                .pointerInput(Unit){
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        )
        {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ){
                Column (
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ){
                    OutlinedTextField(
                        value = name,
                        onValueChange = {name = it},
                        label = { Text("Название")},
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = name.isBlank()  && state is NewMarkerState.Error,
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange ={description = it},
                        label = { Text("Описание")},
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = image,
                        onValueChange = {image = it},
                        label = { Text("Ссылка на фото")},
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        val finalDesc = description.ifBlank { null }
                        val finalImg = image.ifBlank { null }
                        viewModel.onSaveClick(name, finalDesc, finalImg)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = name.isNotBlank() && state !is NewMarkerState.Loading
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "Сохранить маркер")
                }
            }
            if(state is NewMarkerState.Loading){
                CircularProgressIndicator(
                    modifier  = Modifier.align (Alignment.Center)
                )
            }
        }
    }


}