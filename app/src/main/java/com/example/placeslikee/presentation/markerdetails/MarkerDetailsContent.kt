package com.example.placeslikee.presentation.markerdetails

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.presentation.common.AlertDeleteDialog
import com.example.placeslikee.presentation.common.LikeButton
import com.example.placeslikee.presentation.common.LoadingBox
import com.example.placeslikee.presentation.common.OverlayIconButton
import java.util.Locale

@Composable
fun MarkerDetailsContent(
    viewModel: MarkerDetailsViewModel = hiltViewModel(),
    navigateToEdit: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val markerState by viewModel.markerDetails.collectAsState()
    val isSubscribed by viewModel.isSubscribed.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    when (markerState) {
        DetailsState.Idle -> {}

        DetailsState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                LoadingBox()
            }
        }

        is DetailsState.Success -> {
            val marker = (markerState as DetailsState.Success).marker

            if (marker == null) {
                DeletedMarkerState()
            } else {
                MarkerDetailsBody(
                    marker = marker,
                    onToggleLike = { viewModel.onToggleLike() },
                    isCurrAuthor = (viewModel.currUserId == marker.authorId),
                    isSubscribed = isSubscribed,
                    onToggleSubscribe = { viewModel.onToggleSubscribe() },
                    onChangeClick = {
                        navigateToEdit(viewModel.markerId)
                    },
                    onDelete = {
                        showDeleteDialog = true
                    }
                )
                if (showDeleteDialog) {
                    AlertDeleteDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        markerName = marker.name,
                        onConfirmButtonClick = {
                            showDeleteDialog = false
                            viewModel.onDeleteMarker()
                            onDismiss()

                        },
                        onDismissButtonClick = { showDeleteDialog = false }

                    )
                }
            }

        }

    }
}

@Composable
private fun MarkerDetailsBody(
    marker: UIMarker,
    onToggleLike: () -> Unit,
    isCurrAuthor: Boolean = false,
    isSubscribed: Boolean = false,
    onToggleSubscribe: () -> Unit,
    onChangeClick: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onToggleSubscribe()
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        if (marker.image != null) {
            PhotoSection(
                imageUrl = marker.image,
                isCurrAuthor = isCurrAuthor,
                onChangeClick = onChangeClick,
                onDelete = onDelete
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = marker.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (isCurrAuthor && marker.image.isNullOrEmpty()) {
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(top = 2.dp)
                        ){
                        FilledTonalIconButton(
                            onClick = onChangeClick,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Редактировать",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }


                        FilledTonalIconButton(
                            onClick = {  },
                            modifier = Modifier.size(40.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Удалить",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

            }


            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = String.format(
                        Locale.US,
                        "%.5f,  %.5f",
                        marker.latitude,
                        marker.longitude
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            Text(
                text = marker.description ?: "Автор не добавил описание.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                AuthorRow(
                    modifier = Modifier.weight(1f),
                    authorName = marker.authorName
                )

                Spacer(modifier = Modifier.width(12.dp))

                LikeButton(
                    liked = marker.likedByUser,
                    likesAmount = marker.likesAmount,
                    onToggle = onToggleLike
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            if (!isCurrAuthor) {
                Button(
                    onClick = {
                        if (!isSubscribed && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val isPermissionGranted = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                            if (isPermissionGranted) {
                                onToggleSubscribe()
                            } else {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        } else {
                            onToggleSubscribe()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSubscribed) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                        contentColor = if (isSubscribed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(
                        imageVector = if(isSubscribed) Icons.Outlined.CheckCircle else  Icons.Outlined.AddCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSubscribed) "Вы подписаны" else "Подписаться на автора",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun PhotoSection(
    imageUrl: String?,
    isCurrAuthor: Boolean = false,
    onChangeClick: () -> Unit = {},
    onDelete: () -> Unit = {},
) {

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = "Фото локации",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 500.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.FillWidth,
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 2.dp
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        PhotoErrorPlaceholder()
                    }
                }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                PhotoErrorPlaceholder()
            }
        }
        if (isCurrAuthor) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OverlayIconButton(
                    icon = Icons.Outlined.Edit,
                    tint = Color.White,
                    onClick = onChangeClick
                )
                OverlayIconButton(
                    icon = Icons.Outlined.Delete,
                    tint = Color(0xFFFF5252),
                    onClick = onDelete
                )
            }
        }
    }

}

@Composable
private fun PhotoErrorPlaceholder() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Rounded.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Нет фото",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    }
}


@Composable
private fun AuthorRow(authorName: String?, modifier: Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = authorName ?: "Неизвестный",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(16.dp))


    }
}

@Composable
private fun DeletedMarkerState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Место было удалено",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Автор удалил эту локацию.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}