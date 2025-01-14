package com.anshtya.jetx.chats.data.model

import com.anshtya.jetx.common.model.MessageStatus
import com.anshtya.jetx.database.entity.MessageEntity
import com.anshtya.jetx.util.UUIDSerializer
import com.anshtya.jetx.util.ZonedDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime
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
    @SerialName("created_at")
    @Serializable(ZonedDateTimeSerializer::class)
    val createdAt: ZonedDateTime,
    @SerialName("attachment_url")
    val attachmentUrl: String?,
    @SerialName("has_seen")
    val hasSeen: Boolean = false
)

fun NetworkMessage.toEntity(chatId: Int) = MessageEntity(
    id = id,
    senderId = senderId,
    recipientId = recipientId,
    text = text,
    chatId = chatId,
    createdAt = createdAt,
    attachmentUri = attachmentUrl,
    status = if (hasSeen) MessageStatus.SEEN else MessageStatus.RECEIVED
)

fun MessageEntity.toNetworkMessage() = NetworkMessage(
    id = id,
    senderId = senderId,
    recipientId = recipientId,
    text = text,
    createdAt = createdAt,
    attachmentUrl = attachmentUri,
)