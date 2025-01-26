package com.anshtya.jetx.common.model

import com.anshtya.jetx.util.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class NetworkIncomingMessage(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    @SerialName("sender_id")
    @Serializable(UUIDSerializer::class)
    val senderId: UUID,
    @SerialName("recipient_id")
    @Serializable(UUIDSerializer::class)
    val recipientId: UUID,
    val text: String?,
)

fun NetworkIncomingMessage.toIncomingMessage() = IncomingMessage(
    id = id,
    senderId = senderId,
    recipientId = recipientId,
    text = text,
    attachmentUri = null,
    status = MessageStatus.RECEIVED
)