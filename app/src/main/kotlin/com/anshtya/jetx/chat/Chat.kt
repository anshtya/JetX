package com.anshtya.jetx.chat

import java.time.LocalDateTime

data class Chat(
    val id: Int,
    val sender: String,
    val recipient: String,
    val text: String,
    val timestamp: LocalDateTime
)