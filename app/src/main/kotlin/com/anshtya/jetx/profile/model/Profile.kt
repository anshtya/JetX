package com.anshtya.jetx.profile.model

import androidx.compose.ui.graphics.ImageBitmap

data class Profile(
    val name: String,
    val username: String,
    val profilePicture: ImageBitmap?
)
