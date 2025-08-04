package com.anshtya.jetx.database.model

import androidx.room.ColumnInfo
import com.anshtya.jetx.common.model.Chat
import com.anshtya.jetx.util.getDateOrTime
import java.time.ZonedDateTime
import java.util.UUID

data class ChatWithRecentMessage(
    val id: Int,
    @ColumnInfo(name = "recipient_id")
    val recipientId: UUID,
    val username: String,
    @ColumnInfo(name = "profile_picture")
    val profilePicture: String?,
    @ColumnInfo(name = "unread_count")
    val unreadCount: Int,
    @ColumnInfo(name = "recent_message_sender_id")
    val recentMessageSenderId: UUID?,
    @ColumnInfo(name = "recent_message_text")
    val recentMessageText: String?,
    @ColumnInfo(name = "recent_message_status")
    val recentMessageStatus: MessageStatus?,
    @ColumnInfo(name = "recent_message_timestamp")
    val recentMessageTimestamp: ZonedDateTime?,
)

fun ChatWithRecentMessage.toExternalModel(): Chat {
    return Chat(
        id = id,
        recipientId = recipientId,
        username = username,
        profilePicture = profilePicture,
        message = recentMessageText,
        unreadCount = unreadCount,
        timestamp = recentMessageTimestamp?.getDateOrTime(getYesterday = true),
        messageStatus = recentMessageStatus,
        isSender = recipientId != recentMessageSenderId
    )
}
