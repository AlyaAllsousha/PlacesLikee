package com.example.placeslikee.presentation.main

import android.graphics.Paint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.room.util.TableInfo
import androidx.room.util.query
import com.example.placeslikee.R
import com.example.placeslikee.domain.models.NewMarkerIfo
import com.example.placeslikee.presentation.common.DropdownSearchResults
import com.example.placeslikee.presentation.common.SearchBar
import com.example.placeslikee.presentation.list.ListScreen
import com.example.placeslikee.presentation.map.MapScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MainScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCreateMarker: (NewMarkerIfo) -> Unit,

    viewModel: MainViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isMapView by viewModel.isMapView.collectAsState()

    val inputQuery by viewModel.inputQuery.collectAsState()
    val appliedQuery by viewModel.appliedQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var isMapVisible by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    val isImeVisible = WindowInsets.isImeVisible

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    LaunchedEffect(isImeVisible) {
        if (!isImeVisible) {
            focusManager.clearFocus()
        }
    }

    fun navigateSafely(action: () -> Unit) {
        scope.launch {
            isMapVisible = false
            action()
        }
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            MainTopBar(
                userName = user?.name,
                onProfileClick = { navigateSafely { onNavigateToProfile() } },
                onAuthClick = {
                    if (user != null) viewModel.logout()
                    else navigateSafely { onNavigateToAuth() }
                },
                isLoggedIn = user != null,
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBar(
                    query = inputQuery,
                    onQueryChange = viewModel::updateInputQuery,
                    onSearchClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        viewModel.applySearch()
                    },
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 12.dp)
                        .weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilledIconButton(
                    onClick = {
                        viewModel.toggleIsMap()
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    },
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    if (isMapView) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.List,
                            contentDescription = "Показать список"
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.round_map_24),
                            contentDescription = "Показать карту"
                        )
                    }
                }
            }

            Crossfade(targetState = isMapView, label = "map_list_toggle") { showMap ->
                Box(modifier = Modifier.fillMaxSize()) {
                    if (showMap) {
                        isMapVisible = true
                        if (isMapVisible) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 16.dp)
                                    .clip(MaterialTheme.shapes.large)
                            ) {
                                MapScreen(
                                    searchQuery = appliedQuery,
                                    onNavigateToAuth = { navigateSafely { onNavigateToAuth() } },
                                    onNavigateToCreateMarker = {
                                        navigateSafely {
                                            onNavigateToCreateMarker(it)
                                        }
                                    },
                                    onMapClick = {
                                        focusManager.clearFocus()
                                        keyboardController?.hide()
                                    }
                                )

                                RefreshButton(
                                    isRefreshing = isRefreshing,
                                    onClick = { viewModel.refresh() },
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(12.dp)
                                )
                            }
                        }
                    } else {
                        isMapVisible = false
                        ListScreen(searchQuery = appliedQuery)
                    }
                    DropdownSearchResults(
                        visible = inputQuery.isNotEmpty() && searchResults.isNotEmpty() && inputQuery != appliedQuery,
                        results = searchResults,
                        modifier = Modifier.align(Alignment.TopCenter),
                        onItemClick = { markerName ->
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            viewModel.selectPlace(markerName)
                        }
                    )
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> isMapVisible = true
                Lifecycle.Event.ON_PAUSE -> isMapVisible = false
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar(
    userName: String?,
    isLoggedIn: Boolean,
    onProfileClick: () -> Unit,
    onAuthClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title =
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                        .clickable(enabled = isLoggedIn, onClick = onProfileClick)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isLoggedIn)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            AnimatedContent(
                                targetState = userName,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "avatar"
                            ) { name ->
                                if (name != null) {
                                    Text(
                                        text = name.take(1).uppercase(),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Rounded.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = if (isLoggedIn) "Привет," else "Добро пожаловать",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = userName ?: "Гость",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isLoggedIn)
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.outline,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            },
        actions = {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = if (!isLoggedIn)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large)
                    .clickable(onClick = onAuthClick)
            ) {
                Text(
                    text = if (isLoggedIn) "Выйти" else "Войти",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (!isLoggedIn)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                )
            }
        },
        scrollBehavior = scrollBehavior
    )

}


@Composable
private fun RefreshButton(
    isRefreshing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier.size(44.dp),
        shape = MaterialTheme.shapes.medium,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        AnimatedContent(
            targetState = isRefreshing,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "refresh_icon"
        ) { refreshing ->
            if (refreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = "Обновить",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}