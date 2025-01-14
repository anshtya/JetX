package com.anshtya.jetx.chats.data.model

import com.anshtya.jetx.common.model.Message
import java.time.ZonedDateTime

data class DateChatMessages(
    val messages: Map<ZonedDateTime, List<Message>>
)
