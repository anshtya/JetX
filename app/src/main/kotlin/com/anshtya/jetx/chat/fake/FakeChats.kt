package com.anshtya.jetx.chat.fake

import com.anshtya.jetx.chat.Chat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

private val now = LocalDateTime.ofInstant(
    Instant.now(),
    ZoneId.systemDefault()
)
val fakeChats = listOf(
    Chat(
        id = 1,
        sender = "alice_j",
        recipient = "me",
        text = "Hey! Did you check out the new cafe downtown?",
        timestamp = now
    ),
    Chat(
        id = 2,
        sender = "me",
        recipient = "alice_j",
        text = "Not yet! Is it worth visiting?",
        timestamp = now.plusMinutes(1)
    ),
    Chat(
        id = 3,
        sender = "alice_j",
        recipient = "me",
        text = "Absolutely! The mocha is amazing.",
        timestamp = now.plusMinutes(1)
    ),
    Chat(
        id = 3,
        sender = "me",
        recipient = "alice_j",
        text = "Alright, let's go this weekend then!",
        timestamp = now.plusMinutes(2)
    ),
    Chat(
        id = 4,
        sender = "john_doe",
        recipient = "me",
        text = "Can you recommend a good playlist for a long drive?",
        timestamp = now.plusMinutes(45)
    ),
    Chat(
        id = 5,
        sender = "me",
        recipient = "john_doe",
        text = "Sure! Check out my 'Roadtrip Vibes' playlist on Spotify.",
        timestamp = now.plusMinutes(47)
    ),
    Chat(
        id = 6,
        sender = "john_doe",
        recipient = "me",
        text = "Thanks! I'll queue it up.",
        timestamp = now.plusMinutes(47)
    )
)