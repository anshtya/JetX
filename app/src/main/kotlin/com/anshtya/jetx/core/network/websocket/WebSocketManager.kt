package com.anshtya.jetx.core.network.websocket

import com.anshtya.jetx.BuildConfig
import com.anshtya.jetx.auth.data.AuthManager
import com.anshtya.jetx.core.network.di.qualifiers.Base
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor(
    @Base private val client: OkHttpClient,
    private val listener: UpdatesWebSocketListener,
    private val authManager: AuthManager
) {
    var webSocket: WebSocket? = null
        private set

    fun connect() {
        if (webSocket == null) {
            val userId = authManager.authState.value.currentUserIdOrNull()!!
            val request = Request.Builder()
                .url("ws://${BuildConfig.BASE_URL.substringAfter("http://")}connect?userId=$userId")
                .build()
            webSocket = client.newWebSocket(request, listener)
        }
    }

    fun disconnect() {
        webSocket?.cancel()
        webSocket = null
    }
}