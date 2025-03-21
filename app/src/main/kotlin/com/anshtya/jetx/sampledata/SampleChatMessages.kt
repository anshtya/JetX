package com.anshtya.jetx.sampledata

import com.anshtya.jetx.common.model.Message
import com.anshtya.jetx.common.model.MessageStatus
import java.util.UUID

val sampleChatMessages = listOf(
    Message(
        id = 1,
        senderId = UUID.fromString("1"),
        text = "Hey! Did you check out the new cafe downtown?",
        isStarred = false,
        status = MessageStatus.SENT,
        createdAt = "10:00am"
    ),
    Message(
        id = 2,
        senderId = UUID.fromString("me"),
        text = "Absolutely! The mocha is amazing.",
        isStarred = false,
        status = MessageStatus.SENT,
        createdAt = "10:02am"
    )
)