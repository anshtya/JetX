package com.anshtya.jetx.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.anshtya.jetx.common.model.MessageStatus
import java.time.LocalDateTime

@Entity(
    tableName = "message",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["sender_id"],
            onDelete = ForeignKey.CASCADE
        ),

        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipient_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ChatEntity::class,
            parentColumns = ["id"],
            childColumns = ["chat_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(
            value = ["created_at", "sender_id", "recipient_id", "chat_id"],
            name = "index_message_time_sender_recipient_chat"
        )

    ]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "sender_id", index = true)
    val senderId: String,
    @ColumnInfo(name = "recipient_id", index = true)
    val recipientId: String,
    @ColumnInfo(name = "chat_id", index = true)
    val chatId: Int,
    val text: String?,
    @ColumnInfo(name = "is_starred")
    val isStarred: Boolean,
    @ColumnInfo(name = "attachment_uri")
    val attachmentUri: String,
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    val status: MessageStatus
)
