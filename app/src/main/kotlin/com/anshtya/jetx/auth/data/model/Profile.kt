package com.anshtya.jetx.auth.data.model

import androidx.compose.ui.graphics.ImageBitmap

data class Profile(
    val name: String,
    val username: String,
    val profilePicture: ImageBitmap?
)
