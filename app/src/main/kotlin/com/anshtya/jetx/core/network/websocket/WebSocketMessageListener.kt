package com.anshtya.jetx.core.network.websocket

import android.util.Log
import com.anshtya.jetx.core.coroutine.ExternalScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketMessageListener @Inject constructor(
    private val websocketMessageProcessor: WebsocketMessageProcessor,
    @ExternalScope private val scope: CoroutineScope
) : WebSocketListener() {
    private val tag = this::class.simpleName

    override fun onMessage(
        webSocket: WebSocket,
        text: String
    ) {
        scope.launch {
            try {
                websocketMessageProcessor.process(text)
            } catch (e: Exception) {
                Log.w(tag, e.message, e)
            }
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.i(tag, "Connected")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.i(tag, "Connection closed")
    }
}