package com.anshtya.jetx.database.model

import androidx.room.ColumnInfo
import com.anshtya.jetx.common.model.Chat
import com.anshtya.jetx.common.model.MessageStatus
import java.time.LocalDateTime

data class LocalChat(
    val id: Int,
    val username: String,
    @ColumnInfo(name = "profile_picture") val profilePicture: String,
    @ColumnInfo(name = "text") val message: String,
    @ColumnInfo(name = "created_at") val createdAt: LocalDateTime,
    val status: MessageStatus
)

fun LocalChat.toExternalModel(): Chat {
    return Chat(
        id = id,
        name = username,
        profilePicture = profilePicture,
        message = message,
        timestamp = createdAt.toString(),
        status = status
    )
}
