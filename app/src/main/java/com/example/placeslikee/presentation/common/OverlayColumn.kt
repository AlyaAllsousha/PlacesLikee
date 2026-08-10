package com.example.placeslikee.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout

@Composable
fun OverlayColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    overlay: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = {
            Box { content() }
            Box { overlay() }
        }
    ) { measurables, constraints ->
        val contentPlaceable = measurables[0].measure(constraints)
        val overlayPlaceable = measurables[1].measure(
            constraints.copy(minWidth = 0, minHeight = 0)
        )

        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.placeRelative(0, 0)
            overlayPlaceable.placeRelative(0, contentPlaceable.height, zIndex = 1f)
        }
    }
}