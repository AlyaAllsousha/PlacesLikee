package com.example.placeslikee.presentation.favourite


import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel
import com.example.placeslikee.presentation.common.DropdownSearchResults
import com.example.placeslikee.presentation.common.LoadingBox
import com.example.placeslikee.presentation.common.MarkerItem
import com.example.placeslikee.presentation.common.SearchBar
import com.example.placeslikee.presentation.markerdetails.MarkerDetailsContent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FavouriteScreen(
    viewModel: FavouriteViewModel = hiltViewModel(),
    onNavigateToAuth: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    val selectedMarker by viewModel.selectedMarker.collectAsState()

    val inputQuery by viewModel.inputQuery.collectAsState()
    val appliedQuery by viewModel.appliedQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val isImeVisible = WindowInsets.isImeVisible

    LaunchedEffect(isImeVisible) {
        if (!isImeVisible) {
            focusManager.clearFocus()
        }
    }
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .pointerInput(Unit){
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Избранное",
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)

            )
            Spacer(Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxSize()) {
                when (uiState) {
                    FavouriteState.Idle -> {}

                    FavouriteState.Loading -> {
                        LoadingBox()
                    }

                    is FavouriteState.Success -> {
                        val markers = (uiState as FavouriteState.Success).markers

                        if (markers.isEmpty()) {
                            EmptyFavouritesState(
                                modifier = Modifier.padding(paddingValues),
                                title = if(appliedQuery.isBlank()) "Нет понравившихся мест" else "Ничего не найдено"
                            )
                        }
                        else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 8.dp,
                                    bottom = 24.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            )
                            {
                                items(
                                    items = markers,
                                    key = { it.id }
                                ) { marker ->
                                    MarkerItem(
                                        marker = marker,
                                        onLikeClick = {
                                            viewModel.onToggleLike(marker.id)
                                        },
                                        onClick = {
                                            keyboardController?.hide()
                                            focusManager.clearFocus()
                                            viewModel.onMarkerClick(marker.id)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    FavouriteState.Unauthorized -> {
                        UnauthorizedState(
                            modifier = Modifier.padding(paddingValues),
                            onNavigateToAuth = onNavigateToAuth
                        )
                    }
                }
                DropdownSearchResults(
                    visible = inputQuery.isNotEmpty() && searchResults.isNotEmpty() && inputQuery != appliedQuery,
                    results = searchResults,
                    modifier = Modifier.align (Alignment.TopCenter),
                    onItemClick = {markerName ->
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        viewModel.selectPlace(markerName)
                    }
                )
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
private fun EmptyFavouritesState(
    modifier: Modifier = Modifier,
    title: String
    ) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Лайкайте понравившиеся места,\nи они появятся здесь.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun UnauthorizedState(
    onNavigateToAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Войдите в аккаунт",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Чтобы сохранять и просматривать\nизбранные локации, необходимо войти.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(28.dp))
            Button(
                onClick = onNavigateToAuth,
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Войти",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}