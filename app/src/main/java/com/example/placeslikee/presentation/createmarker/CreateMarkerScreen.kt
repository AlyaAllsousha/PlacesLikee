package com.example.placeslikee.presentation.createmarker

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.placeslikee.presentation.common.CustomSnackbar
import com.example.placeslikee.presentation.common.ImagePreviewCard
import com.example.placeslikee.presentation.common.MarkerTextField
import com.example.placeslikee.presentation.common.SaveButton
import com.example.placeslikee.presentation.common.SectionLabel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateMarkerScreen(
    viewModel: CreateMarkerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var selectedImageUri by rememberSaveable { mutableStateOf<String?>(null) }

    val snackBarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val isImeVisible = WindowInsets.isImeVisible

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if(uri != null)
            selectedImageUri = uri.toString()
    }
    LaunchedEffect(isImeVisible) {
        if (!isImeVisible) {
            focusManager.clearFocus()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect { onNavigateBack() }
    }

    LaunchedEffect(state) {
        if (state is NewMarkerState.Error) {
            snackBarHostState.showSnackbar(
                message = (state as NewMarkerState.Error).message
            )
            viewModel.consumeError()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackBarHostState) { snackbarData ->
                CustomSnackbar(
                    snackbarData = snackbarData,
                    isSuccess = true
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Новое место",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .imePadding()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Spacer(modifier = Modifier.height(4.dp))

                    ImagePreviewCard(
                        imageUrl = selectedImageUri,
                        modifier = Modifier.clip(MaterialTheme.shapes.large),
                        onAddOrChangeClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onDeleteClick = {
                            selectedImageUri = null
                        })

                    SectionLabel(text = "Основная информация")

                    MarkerTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Название места",
                        placeholder = "Например: Смотровая площадка",
                        leadingIcon = rememberVectorPainter(image = Icons.Outlined.LocationOn),
                        isError = name.isBlank() && state is NewMarkerState.Error,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)

                    )

                    MarkerTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = "Описание (необязательно)",
                        placeholder = "Расскажите об этом месте...",
                        leadingIcon = rememberVectorPainter(image = Icons.Outlined.Edit),
                        singleLine = false,
                        minLines = 3,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)

                    )

                Spacer(modifier = Modifier.height(8.dp))
            }

            SaveButton(
                isLoading = state is NewMarkerState.Loading,
                isEnabled = name.isNotBlank() && state !is NewMarkerState.Loading,
                text = "Сохранить место",
                onClick = {
                    focusManager.clearFocus()
                    viewModel.onSaveClick(
                        name,
                        description.ifBlank { null },
                        selectedImageUri
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        AnimatedVisibility(
            visible = state is NewMarkerState.Loading,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}
}

