package com.anshtya.jetx.core.network.websocket

import com.anshtya.jetx.util.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import java.util.UUID

@Serializable
data class WebSocketMessage(
    val type: WebSocketMessageType,
    val data: WebSocketMessageData
) {
    companion object {
        fun fromJson(json: String): WebSocketMessage {
            val tempJson = Json.parseToJsonElement(json).jsonObject
            val type = Json.decodeFromJsonElement<WebSocketMessageType>(tempJson["type"]!!)
            val dataElement = tempJson["data"]!!

            val data = when (type) {
                WebSocketMessageType.MESSAGE_UPDATE ->
                    Json.decodeFromJsonElement<WebSocketMessageData.ChatMessageUpdate>(dataElement)
                WebSocketMessageType.PROFILE ->
                    Json.decodeFromJsonElement<WebSocketMessageData.ProfileUpdate>(dataElement)
            }

            return WebSocketMessage(type, data)
        }
    }
}

enum class WebSocketMessageType {
    MESSAGE_UPDATE, PROFILE
}

@Serializable
sealed class WebSocketMessageData {
    @Serializable
    data class ChatMessageUpdate(
        @Serializable(UUIDSerializer::class)
        val id: UUID,
        val received: Boolean,
        val seen: Boolean
    ): WebSocketMessageData()

    // TODO: implement profile updates
    @Serializable
    data object ProfileUpdate: WebSocketMessageData()
}