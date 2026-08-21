package com.example.placeslikee.presentation.common


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.placeslikee.R

@Composable
fun ImagePreviewCard(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    onAddOrChangeClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isProfile:  Boolean = false
) {


    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
            if (imageUrl != null) {
                Box(
                    modifier = modifier
                        .widthIn(min = 160.dp)
                        .heightIn(max = 480.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onAddOrChangeClick() }
                ) {
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 180.dp, max = 480.dp),
                        contentScale = ContentScale.FillWidth,
                        model = imageUrl,
                        contentDescription = "Превью фото",
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(28.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        error = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth().height(200.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.outline_image_24),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Не удалось загрузить фото",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    )
                }
                if (isProfile) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OverlayIconButton(
                            icon = Icons.Outlined.Edit,
                            tint = Color.White,
                            onClick = onAddOrChangeClick
                        )
                        OverlayIconButton(
                            icon = Icons.Outlined.Delete,
                            tint = Color(0xFFFF5252),
                            onClick = onDeleteClick
                        )
                    }
                }
            }
            else{
                Box(modifier = Modifier.clickable { onAddOrChangeClick() }) {
                    ImagePlaceholder()
                }
            }
        }
}

