package com.example.placeslikee.presentation.profile

import android.R.attr.onClick
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.placeslikee.R
import com.example.placeslikee.presentation.common.LoadingBox
import kotlinx.coroutines.launch
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.pointer.pointerInput

import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.zIndex

import com.example.placeslikee.presentation.common.CustomSnackbar
import com.example.placeslikee.presentation.common.DropdownSearchResults
import com.example.placeslikee.presentation.common.MarkerItem
import com.example.placeslikee.presentation.common.OverlayColumn
import com.example.placeslikee.presentation.common.SearchBar
import com.example.placeslikee.presentation.markerdetails.MarkerDetailsContent
import com.example.placeslikee.presentation.profile.dialogs.EditEmailDialog
import com.example.placeslikee.presentation.profile.dialogs.EditNameDialog

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToAuth: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    externalSnackbarMessage: String? = null,
    onClearSnackbarMessage: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val isEmailChanging by viewModel.isEmailChanging.collectAsState()
    val selectedMarker by viewModel.selectedMarker.collectAsState()

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showEditEmailDialog by remember { mutableStateOf(false) }
    var emailDialogServerError by remember { mutableStateOf<String?>(null) }

    val inputQuery by viewModel.inputQuery.collectAsState()
    val applyQuery by viewModel.appliedQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isImeVisible = WindowInsets.isImeVisible

    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    LaunchedEffect(isImeVisible) {
        if (!isImeVisible) {
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            when (message) {
                is ProfileUiEvent.NameChanged -> {
                    launch {
                        snackbarHostState.showSnackbar(
                            message.message
                        )
                    }
                }

                is ProfileUiEvent.EmailChangeEmailSent -> {
                    if (message.result.isSuccess) {
                        showEditEmailDialog = false
                        launch {
                            snackbarHostState.showSnackbar(
                                "Письмо с подтверждением отправлено на новую почту!"
                            )
                        }
                    } else {
                        emailDialogServerError =
                            viewModel.mapErrorToMessage(message.result.exceptionOrNull()?.message)
                    }
                }
            }


        }
    }

    LaunchedEffect(state) {
        if (state is ProfileState.Unauthorized) onNavigateToAuth()
    }

    LaunchedEffect(externalSnackbarMessage) {
        if (externalSnackbarMessage != null) {
            scope.launch {
                snackbarHostState.showSnackbar(externalSnackbarMessage)
            }
            onClearSnackbarMessage()
        }
    }
    when (state) {
        ProfileState.Idle,
        ProfileState.Unauthorized -> {
        }

        ProfileState.Loading -> LoadingBox()

        is ProfileState.Success -> {
            val markers = (state as ProfileState.Success).markersList
            val user = (state as ProfileState.Success).user

            Scaffold(
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        })
                    },

                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Профиль",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = TopAppBarDefaults.largeTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            scrolledContainerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        scrollBehavior = scrollBehavior
                    )
                },
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                        CustomSnackbar(
                            snackbarData = snackbarData
                        )

                    }
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { paddingValues ->


                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(
                        top = 8.dp,
                        bottom = 32.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        UserInfoCard(
                            name = user.name,
                            email = user.email,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            onEditNameClick = { showEditNameDialog = true },
                            onEditEmailClick = { showEditEmailDialog = true }
                        )
                    }

                    item {
                        SectionHeader(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .zIndex(1f),
                            markersCount = markers.size
                        )
                    }
                    stickyHeader {
                        OverlayColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .zIndex(10f)
                                .background(MaterialTheme.colorScheme.background),
                            content = {
                                SearchBar(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 4.dp, bottom = 8.dp),
                                    query = inputQuery,
                                    onQueryChange = viewModel::updateInputQuery,
                                    onSearchClick = {
                                        focusManager.clearFocus()
                                        keyboardController?.hide()
                                        viewModel.applySearch()
                                    }
                                )
                            },
                            overlay = {
                                DropdownSearchResults(
                                    visible = inputQuery.isNotEmpty() && searchResults.isNotEmpty() && inputQuery != applyQuery,
                                    results = searchResults,
                                    onItemClick = { markerName ->
                                        focusManager.clearFocus()
                                        keyboardController?.hide()
                                        viewModel.selectPlace(markerName)
                                    }
                                )
                            }
                        )
                    }



                if (markers.isEmpty() && inputQuery.isBlank()) {
                    item {
                        EmptyMarkersPlaceholder(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            title = "Нет добавленных мест"
                        )
                    }
                } else {
                    if (searchResults.isEmpty() && !applyQuery.isBlank()) {
                        item {
                            EmptyMarkersPlaceholder(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                title = "Ничего не найдено"
                            )
                        }
                    } else {
                        items(items = markers, key = { it.id }) { marker ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)

                            ) {
                                MarkerItem(
                                    marker = marker,

                                    onEditClick = {
                                        onNavigateToEdit(marker.id)
                                    },
                                    onDeleteClick = { viewModel.deleteMarker(marker.id) },
                                    onLikeClick = { viewModel.onToggleLike(marker.id) },
                                    onClick = {
                                        viewModel.onMarkerClick(marker.id)
                                    },
                                    isProfile = true,
                                )
                            }
                        }
                    }
                }

            }

            AnimatedVisibility(
                visible = showEditNameDialog,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                EditNameDialog(
                    currentName = user.name,
                    onDismiss = { showEditNameDialog = false },
                    onConfirm = { newName ->
                        viewModel.onChangeUserInfo(newName)
                        showEditNameDialog = false
                    }
                )
            }

            AnimatedVisibility(
                visible = showEditEmailDialog,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                EditEmailDialog(
                    currentEmail = user.email,
                    onDismiss = {
                        showEditEmailDialog = false
                        emailDialogServerError = null
                    },
                    isLoading = isEmailChanging,
                    serverError = emailDialogServerError,
                    onClearError = { emailDialogServerError = null },
                    onConfirm = { newEmail, password ->
                        emailDialogServerError = null
                        viewModel.onChangeEmail(newEmail, password)
                    }
                )
            }
        }
    }
}
    selectedMarker?.let { marker ->
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.dismissMarkerDetails()
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            MarkerDetailsContent(
                markerId = marker.id,
                navigateToEdit = onNavigateToEdit)
        }
    }
}

@Composable
private fun SectionHeader(
    modifier: Modifier = Modifier,
    markersCount: Int
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Мои места",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = "$markersCount",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun EmptyMarkersPlaceholder(
    modifier: Modifier = Modifier,
    title: String,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.marker_pointer),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Зажмите точку на карте,\nчтобы создать маркер",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}