package com.anshtya.jetx.common.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.anshtya.jetx.R

@Composable
fun ProfilePicture(
    model: Any?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    parentSelected: Boolean = false,
) {
    Box(
        modifier = modifier.clickable { onClick() }
    ) {
        if (model != null) {
            AsyncImage(
                model = model,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.clip(CircleShape)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.blank_profile_picture),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.clip(CircleShape)
            )
        }
        AnimatedVisibility (
            visible = parentSelected,
            enter = scaleIn(animationSpec = spring(stiffness = 1000f)),
            exit = scaleOut(animationSpec = spring(stiffness = 1000f)),
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                tint = Color.White,
                contentDescription = stringResource(id = R.string.chat_selected),
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(2.dp)
                    .size(20.dp)
            )
        }
    }
}

@Preview
@Composable
private fun ProfilePicturePreview() {
    ComponentPreview {
        ProfilePicture(
            model = null,
            parentSelected = true,
            onClick = {},
            modifier = Modifier.size(50.dp)
        )
    }
}