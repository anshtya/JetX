package com.anshtya.jetx.model

import java.time.LocalDateTime

data class ChatMessage(
    val id: Int,
    val sender: String,
    val recipient: String,
    val text: String,
    val timestamp: LocalDateTime
)