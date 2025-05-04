package com.anshtya.jetx.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.anshtya.jetx.common.model.MessageStatus
import java.time.ZonedDateTime
import java.util.UUID

@Entity(
    tableName = "chat",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipient_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(
            value = ["recipient_id"],
            unique = true
        )
    ]
)
data class ChatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "recipient_id")
    val recipientId: UUID,
    @ColumnInfo(name = "unread_count")
    val unreadCount: Int = 0,
    @ColumnInfo(name = "recent_message_sender_id")
    val recentMessageSenderId: UUID? = null,
    @ColumnInfo(name = "recent_message_text")
    val recentMessageText: String? = null,
    @ColumnInfo(name = "recent_message_status")
    val recentMessageStatus: MessageStatus? = null,
    @ColumnInfo(name = "recent_message_timestamp")
    val recentMessageTimestamp: ZonedDateTime? = null,
    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false
)