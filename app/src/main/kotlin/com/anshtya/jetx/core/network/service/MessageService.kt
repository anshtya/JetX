package com.anshtya.jetx.core.network.service

import com.anshtya.jetx.core.model.MessageType
import com.anshtya.jetx.core.network.api.MessageApi
import com.anshtya.jetx.core.network.model.NetworkMessage
import com.anshtya.jetx.core.network.model.NetworkResult
import com.anshtya.jetx.core.network.model.body.MessageUpdateBody
import com.anshtya.jetx.core.network.util.safeApiCall
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageService @Inject constructor(
    private val messageApi: MessageApi
) {
    suspend fun getMessage(
        id: UUID
    ): NetworkResult<NetworkMessage> {
        return safeApiCall {
            messageApi.getMessage(id)
        }
    }

    suspend fun sendMessage(
        id: UUID,
        senderId: UUID,
        type: MessageType,
        targetId: UUID,
        content: String?,
        attachmentId: UUID?
    ): NetworkResult<Unit> {
        return safeApiCall {
            messageApi.sendMessage(
                NetworkMessage(
                    id = id,
                    senderId = senderId,
                    type = type,
                    targetId = targetId,
                    content = content,
                    attachmentId = attachmentId
                )
            )
        }
    }

    suspend fun markMessageReceived(
        id: UUID
    ): NetworkResult<Unit> {
        return safeApiCall {
            messageApi.markMessageReceived(id)
        }
    }

    suspend fun markMessagesSeen(
        ids: List<UUID>
    ): NetworkResult<Unit> {
        return safeApiCall {
            messageApi.markMessagesSeen(
                MessageUpdateBody(ids)
            )
        }
    }

    suspend fun getPendingMessages(): NetworkResult<List<NetworkMessage>> {
        return safeApiCall {
            messageApi.getPendingMessages()
        }
    }
}