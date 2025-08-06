package com.anshtya.jetx.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.anshtya.jetx.R

@Composable
fun ProfilePicture(
    model: Any?,
    modifier: Modifier = Modifier,
    shape: Shape? = null,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (model != null) {
        AsyncImage(
            model = model,
            contentDescription = null,
            contentScale = contentScale,
            modifier = modifier.clip(shape ?: CircleShape)
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.blank_profile_picture),
            contentDescription = null,
            contentScale = contentScale,
            modifier = modifier.clip(shape ?: CircleShape)
        )
    }
}

@Preview
@Composable
private fun ProfilePicturePreview() {
    ProfilePicture(
        model = null,
        modifier = Modifier.size(50.dp)
    )
}