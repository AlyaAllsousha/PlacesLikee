package com.example.placeslikee.presentation.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import com.example.placeslikee.R
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.placeslikee.domain.models.UIMarker
import com.google.android.gms.tasks.Tasks.await
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToAuth: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val isEmailChanging by viewModel.isEmailChanging.collectAsState()
    var showEditNameDialog by remember { mutableStateOf(false) }
    var showEditEmailDialog by remember { mutableStateOf(false) }
    var emailDialogServerError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }


    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            if (message.isSuccess) {
                showEditEmailDialog = false
                launch {
                    snackbarHostState.showSnackbar("Письмо с подтверждением отправлено на новую почту!")
                }
            } else {
                emailDialogServerError =  viewModel.mapErrorToMessage(message.exceptionOrNull()?.message)
            }
        }
    }
    LaunchedEffect(state) {
        if (state is ProfileState.Unauthorized) {
            onNavigateToAuth()
        }
    }
    when (state) {
        ProfileState.Idle -> {}
        ProfileState.Unauthorized -> {}

        ProfileState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        is ProfileState.Success -> {
            Scaffold(
                topBar = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Профиль",
                            style = MaterialTheme.typography.headlineLarge
                        )

                    }
                },
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
            ) { paddingValues ->
                val markers = (state as ProfileState.Success).markersList
                val user = (state as ProfileState.Success).user
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        UserInfoCard(
                            name = user.name,
                            email = user.email,
                            onEditNameClick = { showEditNameDialog = true },
                            onEditEmailClick = { showEditEmailDialog = true }
                        )
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Мои места:",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold

                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${markers.size}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    if (markers.isEmpty()) {
                        item {
                            EmptyMarkersPlaceholder()
                        }
                    } else {
                        items(
                            items = markers,
                            key = { it.id }
                        ) { marker ->
                            MarkerItem(
                                marker = marker,
                                onEditClick = { },
                                onDeleteClick = { viewModel.deleteMarker(marker.id) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }

                if (showEditNameDialog) {
                    EditNameDialog(
                        currentName = user.name,
                        onDismiss = { showEditNameDialog = false },
                        onConfirm = { newName ->
                            viewModel.onChangeUserInfo(newName)
                            showEditNameDialog = false
                        }
                    )
                }

                if (showEditEmailDialog) {
                    EditEmailDialog(
                        currentEmail = user.email,
                        onDismiss = {
                            showEditEmailDialog = false
                            emailDialogServerError = null },
                        isLoading = isEmailChanging,
                        serverError = emailDialogServerError,
                        onClearError = {
                            emailDialogServerError = null
                        },
                        onConfirm = { newEmail, password ->
                            emailDialogServerError = null
                            viewModel.onChangeEmail(newEmail, password)
                        }
                    )
                }
            }

        }

    }
}


@Composable
fun EmptyMarkersPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.marker_pointer),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "У вас пока нет маркеров",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = "Зажмите точку на карте, чтобы создать новый маркер",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}





