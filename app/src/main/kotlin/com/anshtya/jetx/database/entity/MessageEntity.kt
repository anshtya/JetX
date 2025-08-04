package com.anshtya.jetx.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.anshtya.jetx.database.model.MessageStatus
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
        Index(value = ["chat_id", "created_at", "status"])
    ]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uid: UUID,
    @ColumnInfo(name = "sender_id", index = true)
    val senderId: UUID,
    @ColumnInfo(name = "recipient_id", index = true)
    val recipientId: UUID,
    @ColumnInfo(name = "chat_id")
    val chatId: Int,
    val text: String?,
    @ColumnInfo(name = "is_starred")
    val isStarred: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: ZonedDateTime,
    val status: MessageStatus
)