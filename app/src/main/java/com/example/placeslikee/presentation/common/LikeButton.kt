package com.example.placeslikee.presentation.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun LikeButton(
    liked: Boolean,
    likesAmount: Int,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (liked) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "like_scale"
    )

    val iconTint by animateColorAsState(
        targetValue = if (liked)
            MaterialTheme.colorScheme.secondary
        else
            MaterialTheme.colorScheme.outline,
        label = "like_color"
    )

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = if (liked)
            MaterialTheme.colorScheme.secondaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            IconButton(
                onClick = onToggle,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = if (liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Лайк",
                    tint = iconTint,
                    modifier = Modifier
                        .size(16.dp)
                        .scale(scale)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$likesAmount",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = iconTint
            )
        }
    }
}
