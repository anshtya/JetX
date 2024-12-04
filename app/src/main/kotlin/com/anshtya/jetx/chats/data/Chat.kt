package com.anshtya.jetx.chats.data

import androidx.annotation.DrawableRes

data class Chat(
    val id: Int,
    val name: String,
    @DrawableRes val picture: Int?,
    val message: String,
    val timeStamp: String
)
