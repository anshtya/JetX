package com.anshtya.jetx.chats.ui.chat

import com.anshtya.jetx.chats.ui.navigation.ChatsDestination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.UUID

class ChatTargetTest {
    private val recipientId = UUID.randomUUID()

    @Test
    fun `chat id resolves to an existing chat`() {
        val target = ChatsDestination.Chat(chatId = 5).toChatTarget()

        assertEquals(ChatTarget.ExistingChat(5), target)
    }

    @Test
    fun `chat id takes precedence over recipient`() {
        val target = ChatsDestination.Chat(
            chatId = 5,
            recipientId = recipientId.toString(),
            username = "user"
        ).toChatTarget()

        assertEquals(ChatTarget.ExistingChat(5), target)
    }

    @Test
    fun `recipient with username resolves to a new chat`() {
        val target = ChatsDestination.Chat(
            recipientId = recipientId.toString(),
            username = "user",
            pictureUrl = "pic"
        ).toChatTarget()

        assertEquals(ChatTarget.NewChat(recipientId, "user", "pic"), target)
    }

    @Test
    fun `new chat picture is optional`() {
        val target = ChatsDestination.Chat(
            recipientId = recipientId.toString(),
            username = "user"
        ).toChatTarget()

        assertEquals(ChatTarget.NewChat(recipientId, "user", null), target)
    }

    @Test
    fun `no arguments resolve to null`() {
        assertNull(ChatsDestination.Chat().toChatTarget())
    }

    @Test
    fun `recipient without username resolves to null`() {
        val target = ChatsDestination.Chat(recipientId = recipientId.toString()).toChatTarget()

        assertNull(target)
    }

    @Test
    fun `malformed recipient id resolves to null`() {
        val target = ChatsDestination.Chat(
            recipientId = "not-a-uuid",
            username = "user"
        ).toChatTarget()

        assertNull(target)
    }

    @Test
    fun `username without recipient resolves to null`() {
        assertNull(ChatsDestination.Chat(username = "user").toChatTarget())
    }
}
