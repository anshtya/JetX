package com.anshtya.jetx.core.network.websocket

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.anshtya.jetx.BuildConfig
import com.anshtya.jetx.auth.data.AuthManager
import com.anshtya.jetx.core.network.di.qualifiers.Base
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import javax.inject.Inject
import javax.inject.Singleton

// TODO: needs more validation
@Singleton
class WebSocketManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @Base private val client: OkHttpClient,
    private val listener: WebSocketMessageListener,
    private val authManager: AuthManager
) {
    private val tag = this::class.simpleName

    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var isConnecting = false
    private var webSocket: WebSocket? = null

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            if (webSocket == null && !isConnecting) {
                Log.i(tag, "Internet restored, reconnecting WebSocket")
                openNewConnection()
            }
        }

        override fun onLost(network: Network) {
            Log.i(tag, "Connection lost, disconnecting WebSocket")
            disconnect()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            capabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, capabilities)
            val hasInternet = capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET
            )
            if (hasInternet && webSocket == null) {
                Log.i(tag, "Capabilities changed, reconnecting WebSocket")
                openNewConnection()
            }
        }
    }

    fun connect() {
        openNewConnection()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun openNewConnection() {
        if (webSocket == null) {
            isConnecting = true
            Log.i(tag, "Connecting WebSocket")
            val userId = authManager.authState.value.currentUserIdOrNull()!!
            val request = Request.Builder()
                .url("ws://${BuildConfig.BASE_URL.substringAfter("http://")}connect?userId=$userId")
                .build()
            webSocket = client.newWebSocket(request, listener)
            isConnecting = false
        }
    }

    fun disconnect() {
        Log.i(tag, "Disconnecting WebSocket")
        webSocket?.cancel()
        webSocket = null
    }
}