package com.anshtya.jetx.chats.data.model

import com.anshtya.jetx.common.util.UUIDSerializer
import com.anshtya.jetx.database.entity.MessageEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class NetworkMessageRequest(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    @SerialName("sender_id")
    @Serializable(UUIDSerializer::class)
    val senderId: UUID,
    @SerialName("recipient_id")
    @Serializable(UUIDSerializer::class)
    val recipientId: UUID,
    val text: String?,
    @SerialName("attachment_url")
    val attachmentUrl: String?,
    @SerialName("has_seen")
    val hasSeen: Boolean = false
)

fun MessageEntity.toNetworkMessageRequest() = NetworkMessageRequest(
    id = id,
    senderId = senderId,
    recipientId = recipientId,
    text = text,
    attachmentUrl = attachmentUri,
)