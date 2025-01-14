package com.anshtya.jetx.chats.data.model

import com.anshtya.jetx.common.model.Message

data class DateChatMessages(
    val messages: Map<String, List<Message>>
)
