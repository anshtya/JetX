package com.anshtya.jetx.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.anshtya.jetx.R

@Composable
fun ProfilePicture(
    image: ImageBitmap?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (image != null) {
        Image(
            bitmap = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .clip(CircleShape)
                .clickable { onClick() }
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.blank_profile_picture),
            contentDescription = null,
            modifier = modifier
                .clip(CircleShape)
                .clickable { onClick() }
        )
    }
}