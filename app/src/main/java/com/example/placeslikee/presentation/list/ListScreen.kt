package com.example.placeslikee.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.placeslikee.presentation.common.LoadingBox
import com.example.placeslikee.presentation.common.MarkerItem
import com.example.placeslikee.presentation.markerdetails.MarkerDetailsContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    viewModel: ListViewModel = hiltViewModel(),
    searchQuery: String = "",
    ){

    val state by viewModel.uiState.collectAsState()
    val selectedMarker by viewModel.selectedMarker.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(searchQuery) {
        viewModel.setSearchQuery(searchQuery)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        when (val currentState = state) {
            is ListState.Loading -> {
                LoadingBox()
            }

            is ListState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = currentState.message,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            is ListState.Success -> {
                if (currentState.markers.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Ничео не найдено",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = currentState.markers,
                            key = { it.id }
                        ) { marker ->
                            MarkerItem(
                                marker = marker,
                                onClick = { viewModel.selectMarker(marker) },
                                onLikeClick = { viewModel.onToggleLike(marker.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    selectedMarker?.let { marker ->
        ModalBottomSheet (
            onDismissRequest = { viewModel.dismissMarkerDetails() },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            MarkerDetailsContent(marker.id)
        }
    }
}

