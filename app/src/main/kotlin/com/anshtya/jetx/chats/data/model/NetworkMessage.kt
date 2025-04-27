package com.anshtya.jetx.chats.data.model

import com.anshtya.jetx.database.entity.MessageEntity
import com.anshtya.jetx.util.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class NetworkMessage(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    @SerialName("sender_id")
    @Serializable(UUIDSerializer::class)
    val senderId: UUID,
    @SerialName("recipient_id")
    @Serializable(UUIDSerializer::class)
    val recipientId: UUID,
    val text: String?,
    @SerialName("attachment_id")
    val attachmentId: Int?,
    @SerialName("has_seen")
    val hasSeen: Boolean,
    @SerialName("has_received")
    val hasReceived: Boolean
)

fun MessageEntity.toNetworkMessage(attachmentId: Int?) = NetworkMessage(
    id = uid,
    senderId = senderId,
    recipientId = recipientId,
    text = text,
    attachmentId = attachmentId,
    hasSeen = false,
    hasReceived = false
)