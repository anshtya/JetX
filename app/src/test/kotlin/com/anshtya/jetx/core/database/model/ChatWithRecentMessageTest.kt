package com.anshtya.jetx.core.database.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

class ChatWithRecentMessageTest {
    private val recipientId = UUID.randomUUID()
    private val currentUserId = UUID.randomUUID()

    private fun chat(
        senderId: UUID,
        text: String? = "hello",
        status: MessageStatus = MessageStatus.SENT,
        timestamp: ZonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"))
    ) = ChatWithRecentMessage(
        id = 7,
        recipientId = recipientId,
        username = "user",
        profilePicture = "pic",
        unreadCount = 3,
        senderId = senderId,
        messageText = text,
        messageStatus = status,
        messageTimestamp = timestamp
    )

    @Test
    fun `maps all fields to the external model`() {
        val result = chat(senderId = currentUserId, status = MessageStatus.SEEN).toExternalModel()

        assertEquals(7, result.id)
        assertEquals(recipientId, result.recipientId)
        assertEquals("user", result.username)
        assertEquals("pic", result.profilePicture)
        assertEquals("hello", result.message)
        assertEquals(3, result.unreadCount)
        assertEquals(MessageStatus.SEEN, result.messageStatus)
    }

    @Test
    fun `isSender is true when the recent message is not from the recipient`() {
        assertTrue(chat(senderId = currentUserId).toExternalModel().isSender)
    }

    @Test
    fun `isSender is false when the recent message is from the recipient`() {
        assertFalse(chat(senderId = recipientId).toExternalModel().isSender)
    }

    @Test
    fun `missing message text maps to an empty message`() {
        assertEquals("", chat(senderId = recipientId, text = null).toExternalModel().message)
    }

    @Test
    fun `timestamp is formatted as time for a message sent today`() {
        val now = ZonedDateTime.now()
        val result = chat(senderId = recipientId, timestamp = now).toExternalModel()

        // Same-day messages are shown as a time ("h:mm a"), not a date or "Yesterday".
        assertTrue(result.timestamp.contains(":"))
        assertFalse(result.timestamp.contains("/"))
    }

    @Test
    fun `timestamp is Yesterday for a message sent yesterday`() {
        val yesterday = ZonedDateTime.now().minusDays(1)
        val result = chat(senderId = recipientId, timestamp = yesterday).toExternalModel()

        assertEquals("Yesterday", result.timestamp)
    }
}
