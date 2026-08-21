package com.example.placeslikee.presentation.profile.editMarker

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.placeslikee.R
import com.example.placeslikee.presentation.common.ErrorBox
import com.example.placeslikee.presentation.common.ImagePreviewCard
import com.example.placeslikee.presentation.common.LoadingBox
import com.example.placeslikee.presentation.common.MarkerTextField
import com.example.placeslikee.presentation.common.SaveButton
import com.example.placeslikee.presentation.common.SectionLabel
import com.example.placeslikee.presentation.newmarker.NewMarkerState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMarkerScreen(
    viewModel: EditMarkerViewModel = hiltViewModel(),
    onNavigateBack: (String?) -> Unit
) {
    val state by viewModel.editMarkerState.collectAsState()


    val focusManager = LocalFocusManager.current
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val isMarkerUpdating by viewModel.markerIsUpdating.collectAsState()




    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { result ->
            when (result) {
                is EditMarkerEvent.Error -> {
                    errorMessage = result.message
                }

                is EditMarkerEvent.Succeed -> {
                    onNavigateBack(result.message)
                }
            }
        }
    }


    Scaffold(

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Изменить место",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack(null) }) {
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

        when (state) {
            EditMarkerState.Idle -> {}
            EditMarkerState.Loading -> {
                LoadingBox()
            }

            is EditMarkerState.Error -> {
                val errorMessage = (state as EditMarkerState.Error).message
                ErrorBox(
                    message = errorMessage,
                    onActionClick = {
                        viewModel.reloadMarker()
                    }
                )
            }

            is EditMarkerState.Success -> {
                val marker = (state as EditMarkerState.Success).marker
                var latStr by remember(marker.id) { mutableStateOf(marker.latitude.toString()) }
                var lonStr by remember(marker.id) { mutableStateOf(marker.longitude.toString()) }
                var name by remember(marker.id) { mutableStateOf(marker.name) }
                var description by remember(marker.id) { mutableStateOf(marker.description) }
                var selectedImageUri by remember(marker.id) { mutableStateOf<String?>(marker.image) }

                val photoPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia()
                ) { uri ->
                    if (uri != null)
                        selectedImageUri = uri.toString()
                }

                val parsedLat = latStr.toDoubleOrNull()
                val parsedLon = lonStr.toDoubleOrNull()

                val isLatValid = parsedLat != null && parsedLat in -90.0..90.0
                val isLonValid = parsedLon != null && parsedLon in -180.0..180.0

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
                        Spacer(modifier = Modifier.height(8.dp))

                        SectionLabel(text = "Основная информация")

                        MarkerTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Название места",
                            placeholder = "Например: Смотровая площадка",
                            leadingIcon = rememberVectorPainter(image = Icons.Outlined.LocationOn),
                            isError = name.isBlank(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)

                        )
                        AnimatedVisibility(visible = name.isBlank()) {
                            Text(
                                text = "У места должно быть имя",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }

                        MarkerTextField(
                            value = description ?: "",
                            onValueChange = { description = it },
                            label = "Описание",
                            placeholder = "Расскажите об этом месте...",
                            leadingIcon = rememberVectorPainter(image = Icons.Outlined.Edit),
                            singleLine = false,
                            minLines = 3,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)

                        )
                        SectionLabel(text = "Координаты")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MarkerTextField(
                                value = latStr,
                                onValueChange = { input ->
                                    latStr = input.replace(",", ".").trim()
                                },
                                label = "Широта",
                                placeholder = "-90.0 до 90.0",
                                singleLine = true,
                                isError = !isLatValid,
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )

                            MarkerTextField(
                                value = lonStr,
                                onValueChange = { input ->
                                    lonStr = input.replace(",", ".").trim()
                                },
                                label = "Долгота",
                                placeholder = "-180.0 до 180.0",
                                singleLine = true,
                                isError = !isLonValid,
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }

                        AnimatedVisibility(visible = !isLatValid || !isLonValid) {
                            Text(
                                text = "Координаты указаны неверно. Используйте только числа и точку.",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }


                        Spacer(modifier = Modifier.weight(1f, fill = false))
                        Spacer(modifier = Modifier.height(16.dp))

                        SaveButton(
                            isLoading = isMarkerUpdating,
                            isEnabled = name.isNotBlank() && isLatValid && isLonValid && !isMarkerUpdating,
                            text = "Сохранить изменения",
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.onSubmit(
                                    lat = latStr.toDouble(),
                                    longitude = lonStr.toDouble(),
                                    name = name.trim(),
                                    description = description?.trim()?.ifBlank { null },
                                    image = selectedImageUri
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    AnimatedVisibility(
                        visible = isMarkerUpdating,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}





