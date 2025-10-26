package com.anshtya.jetx.core.network.websocket

import android.util.Log
import com.anshtya.jetx.core.coroutine.DefaultDispatcher
import com.anshtya.jetx.core.database.datasource.LocalMessagesDataSource
import com.anshtya.jetx.core.database.model.MessageStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdatesWebSocketListener @Inject constructor(
    private val localMessagesDataSource: LocalMessagesDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher
) : WebSocketListener() {
    private val tag = this::class.simpleName
    private val coroutineScope = CoroutineScope(dispatcher + Job())

    override fun onMessage(
        webSocket: WebSocket,
        text: String
    ) {
        try {
            val webSocketMessage = WebSocketMessage.fromJson(text)
            when (webSocketMessage.type) {
                WebSocketMessageType.MESSAGE_UPDATE -> {
                    val messageUpdate = webSocketMessage.data as WebSocketMessageData.ChatMessageUpdate
                    coroutineScope.launch {
                        localMessagesDataSource.updateMessageStatus(
                            messageId = messageUpdate.id,
                            status = if (messageUpdate.seen) {
                                MessageStatus.SEEN
                            } else if (messageUpdate.received) {
                                MessageStatus.RECEIVED
                            } else null
                        )
                    }
                }

                WebSocketMessageType.PROFILE -> {}
            }
        } catch (e: Exception) {
            Log.w(tag, e.message, e)
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.i(tag, "Connected")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        coroutineScope.cancel()
        Log.i(tag, "Cancelling scope")
    }
}