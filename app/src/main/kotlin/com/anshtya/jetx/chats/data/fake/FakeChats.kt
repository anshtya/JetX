package com.anshtya.jetx.chats.data.fake

import com.anshtya.jetx.chats.data.Chat
import com.anshtya.jetx.fakedata.fakeChatMessages
import com.anshtya.jetx.fakedata.fakeUsers
import java.time.format.DateTimeFormatter

val fakeChats = fakeChatMessages
    .groupBy { it.sender }
    .filter { (sender, _) -> sender != "me" }
    .map { (_, chats) -> chats.maxByOrNull { it.timestamp } }
    .filterNotNull()
    .map { chat ->
        Chat(
            id = chat.id,
            name = chat.sender,
            picture = fakeUsers.find { it.username == chat.sender }?.photo,
            message = chat.text,
            timeStamp = DateTimeFormatter.ofPattern("h:mm a").format(chat.timestamp)
        )
    }


