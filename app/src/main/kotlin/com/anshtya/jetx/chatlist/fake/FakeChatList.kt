package com.anshtya.jetx.chatlist.fake

import com.anshtya.jetx.chat.fake.fakeChats
import com.anshtya.jetx.chat.fake.fakeUsers
import com.anshtya.jetx.chatlist.ChatListItem

val fakeChatList = fakeChats
    .groupBy { it.sender }
    .filter { (sender, _) -> sender != "me" }
    .map { (_, chats) -> chats.maxByOrNull { it.timestamp } }
    .filterNotNull()
    .map { chat ->
        ChatListItem(
            id = chat.id,
            name = chat.sender,
            picture = fakeUsers.find { it.username == chat.sender }?.photo,
            message = chat.text
        )
    }
