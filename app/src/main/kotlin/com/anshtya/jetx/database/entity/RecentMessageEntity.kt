package com.anshtya.jetx.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "recent_message",
    foreignKeys = [
        ForeignKey(
            entity = ChatEntity::class,
            parentColumns = ["id"],
            childColumns = ["chat_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["message_id"]
        )
    ]
)
data class RecentMessageEntity(
    @PrimaryKey
    @ColumnInfo(name = "chat_id")
    val chatId: Int,

    @ColumnInfo(name = "message_id", index = true)
    val messageId: Int?
)