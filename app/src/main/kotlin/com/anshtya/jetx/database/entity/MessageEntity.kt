package com.anshtya.jetx.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.anshtya.jetx.common.model.Message
import com.anshtya.jetx.common.model.MessageStatus
import com.anshtya.jetx.util.getDateOrTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

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
    @PrimaryKey
    val id: UUID,
    @ColumnInfo(name = "sender_id", index = true)
    val senderId: UUID,
    @ColumnInfo(name = "recipient_id", index = true)
    val recipientId: UUID,
    @ColumnInfo(name = "chat_id", index = true)
    val chatId: Int,
    val text: String?,
    @ColumnInfo(name = "is_starred")
    val isStarred: Boolean = false,
    @ColumnInfo(name = "attachment_uri")
    val attachmentUri: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: ZonedDateTime = ZonedDateTime.now(ZoneId.of("UTC")),
    val status: MessageStatus
)

fun MessageEntity.toExternalModel(): Message {
    return Message(
        id = id,
        senderId = senderId,
        text = text ?: "",
        isStarred = isStarred,
        createdAt = createdAt.getDateOrTime(getTimeOnly = true),
        status = status
    )
}