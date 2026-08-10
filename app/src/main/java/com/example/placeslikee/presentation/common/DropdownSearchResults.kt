package com.example.placeslikee.presentation.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.placeslikee.domain.models.UIMarker
import kotlin.time.Duration.Companion.nanoseconds

@Composable
fun DropdownSearchResults(
    visible: Boolean,
    results: List<UIMarker>,
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .heightIn(max = 250.dp),
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            LazyColumn {
                items(results) { marker ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onItemClick(marker.name)
                            }
                            .padding(16.dp)
                    ) {
                        Text(
                            text = marker.name,
                            fontWeight = FontWeight.Bold,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "от ${marker.authorName}",
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                }
            }

        }
    }
}