package com.anshtya.jetx.database.model

import androidx.room.ColumnInfo
import java.util.UUID

data class ChatIds(
    val id: Int,
    @ColumnInfo(name = "recipient_id") val recipientId: UUID
)
