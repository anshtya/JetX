package com.anshtya.jetx.core.network.websocket

import com.anshtya.jetx.chats.data.MessagesRepository
import com.anshtya.jetx.core.coroutine.DefaultDispatcher
import com.anshtya.jetx.core.database.datasource.LocalMessagesDataSource
import com.anshtya.jetx.core.database.model.MessageStatus
import com.anshtya.jetx.core.network.model.NetworkMessage
import com.anshtya.jetx.core.network.websocket.message.ChatMessageUpdate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebsocketMessageProcessor @Inject constructor(
    private val localMessagesDataSource: LocalMessagesDataSource,
    private val messagesRepository: MessagesRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    suspend fun process(message: String) {
        val tempJson = json.parseToJsonElement(message).jsonObject
        val type = json.decodeFromJsonElement<WebSocketMessageType>(tempJson["type"]!!)
        val dataElement = tempJson["data"]!!

        when (type) {
            WebSocketMessageType.NEW_MESSAGE -> {
                val incomingMessage = withContext(defaultDispatcher) {
                    json.decodeFromJsonElement<NetworkMessage>(dataElement)
                }
                messagesRepository.receiveChatMessage(
                    id = incomingMessage.id,
                    senderId = incomingMessage.senderId,
                    recipientId = incomingMessage.targetId,
                    text = incomingMessage.content,
                    attachmentId = incomingMessage.attachmentId
                ).getOrThrow()
            }

            WebSocketMessageType.MESSAGE_UPDATE -> {
                val chatMessageUpdate = withContext(defaultDispatcher) {
                    json.decodeFromJsonElement<ChatMessageUpdate>(dataElement)
                }
                localMessagesDataSource.updateMessageStatus(
                    messageId = chatMessageUpdate.id,
                    status = if (chatMessageUpdate.seen) {
                        MessageStatus.SEEN
                    } else if (chatMessageUpdate.received) {
                        MessageStatus.RECEIVED
                    } else null
                )
            }

            else -> {}
        }
    }
}