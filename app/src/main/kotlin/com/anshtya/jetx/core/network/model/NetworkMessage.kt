package com.anshtya.jetx.core.network.model

import com.anshtya.jetx.core.model.MessageType
import com.anshtya.jetx.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class NetworkMessage(
    @Serializable(UUIDSerializer::class)
    val id: UUID,

    @Serializable(UUIDSerializer::class)
    val senderId: UUID,

    val type: MessageType,

    @Serializable(UUIDSerializer::class)
    val targetId: UUID,

    val content: String?,

    @Serializable(UUIDSerializer::class)
    val attachmentId: UUID?
)
