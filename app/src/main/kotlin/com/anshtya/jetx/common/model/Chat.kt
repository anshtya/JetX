package com.anshtya.jetx.common.model

data class Chat(
    val id: Int,
    val name: String,
    val profilePicture: String?,
    val message: String,
    val timestamp: String,
    val status: MessageStatus
)
