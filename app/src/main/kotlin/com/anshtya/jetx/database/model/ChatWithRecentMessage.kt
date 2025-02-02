package com.anshtya.jetx.database.model

import androidx.room.ColumnInfo
import com.anshtya.jetx.common.model.Chat
import com.anshtya.jetx.common.model.MessageStatus
import com.anshtya.jetx.util.getDateOrTime
import java.time.ZonedDateTime
import java.util.UUID

data class ChatWithRecentMessage(
    val id: Int,
    @ColumnInfo(name = "recipient_id")
    val recipientId: UUID,
    @ColumnInfo(name = "sender_id")
    val senderId: UUID,
    val username: String,
    @ColumnInfo(name = "profile_picture")
    val profilePicture: String?,
    @ColumnInfo(name = "text")
    val message: String,
    @ColumnInfo(name = "unread_count")
    val unreadCount: Int,
    @ColumnInfo(name = "created_at")
    val createdAt: ZonedDateTime,
    @ColumnInfo(name = "status")
    val messageStatus: MessageStatus
)

fun ChatWithRecentMessage.toExternalModel(): Chat {
    return Chat(
        id = id,
        recipientId = recipientId,
        username = username,
        profilePicture = profilePicture,
        message = message,
        unreadCount = unreadCount,
        timestamp = createdAt.getDateOrTime(getYesterday = true),
        messageStatus = messageStatus,
        isSender = recipientId != senderId
    )
}
