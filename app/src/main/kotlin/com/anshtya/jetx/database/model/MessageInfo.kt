package com.anshtya.jetx.database.model

import androidx.room.ColumnInfo
import java.time.ZonedDateTime
import java.util.UUID

data class MessageInfo(
    val id: Int,
    @ColumnInfo(name = "sender_id")
    val senderId: UUID,
    @ColumnInfo(name = "chat_id")
    val chatId: Int,
    val text: String?,
    @ColumnInfo(name = "is_starred")
    val isStarred: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: ZonedDateTime,
    val status: MessageStatus
)
