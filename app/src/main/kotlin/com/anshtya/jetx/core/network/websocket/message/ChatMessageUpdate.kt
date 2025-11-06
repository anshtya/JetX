package com.anshtya.jetx.core.network.websocket.message

import com.anshtya.jetx.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

data class ChatMessageUpdate(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val received: Boolean,
    val seen: Boolean
)