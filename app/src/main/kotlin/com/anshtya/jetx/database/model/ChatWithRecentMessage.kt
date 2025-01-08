package com.anshtya.jetx.database.model

import androidx.room.ColumnInfo
import com.anshtya.jetx.common.model.Chat
import com.anshtya.jetx.common.model.MessageStatus
import com.anshtya.jetx.common.util.formattedString
import java.time.ZonedDateTime

data class ChatWithRecentMessage(
    val id: Int,
    val username: String,
    @ColumnInfo(name = "profile_picture") val profilePicture: String?,
    @ColumnInfo(name = "text") val message: String,
    @ColumnInfo(name = "created_at") val createdAt: ZonedDateTime,
    @ColumnInfo(name = "status") val messageStatus: MessageStatus
)

fun ChatWithRecentMessage.toExternalModel(): Chat {
    return Chat(
        id = id,
        username = username,
        profilePicture = profilePicture,
        message = message,
        timestamp = createdAt.formattedString(),
        messageStatus = messageStatus
    )
}
