package com.anshtya.jetx.common.model

data class Chat(
    val id: Int,
    val username: String,
    val profilePicture: String?,
    val message: String,
    val timestamp: String,
    val messageStatus: MessageStatus
)
