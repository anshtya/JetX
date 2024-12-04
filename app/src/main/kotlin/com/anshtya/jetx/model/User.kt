package com.anshtya.jetx.model

import androidx.annotation.DrawableRes

data class User(
    val id: Int,
    val name: String,
    val username: String,
    @DrawableRes val photo: Int?,
    val bio: String
)
