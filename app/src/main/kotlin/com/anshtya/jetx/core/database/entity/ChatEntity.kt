package com.anshtya.jetx.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
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
    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false
)