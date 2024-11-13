package com.anshtya.jetx.chatlist

import androidx.annotation.DrawableRes

data class ChatListItem(
    val id: Int,
    val name: String,
    @DrawableRes val picture: Int?,
    val message: String
)
