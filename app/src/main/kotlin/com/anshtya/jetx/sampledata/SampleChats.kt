package com.anshtya.jetx.sampledata

import com.anshtya.jetx.common.model.Chat
import com.anshtya.jetx.common.model.MessageStatus
import java.util.UUID

val sampleChats = listOf(
    Chat(
        id = 1,
        recipientId = UUID.fromString("1"),
        username = "user1",
        profilePicture = null,
        message = "How are you",
        timestamp = "10:00am",
        messageStatus = MessageStatus.SENT
    ),
    Chat(
        id = 2,
        recipientId = UUID.fromString("2"),
        username = "user2",
        profilePicture = null,
        message = "Call me @3pm",
        timestamp = "10:30am",
        messageStatus = MessageStatus.SENT
    ),
)