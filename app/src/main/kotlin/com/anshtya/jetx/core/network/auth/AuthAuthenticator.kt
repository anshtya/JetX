package com.anshtya.jetx.core.network.auth

import android.util.Log
import com.anshtya.jetx.auth.data.AuthManager
import com.anshtya.jetx.core.network.util.HEADER_AUTHORIZATION_KEY
import com.anshtya.jetx.core.network.util.HEADER_BEARER_PREFIX
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthAuthenticator @Inject constructor(
    private val authManager: AuthManager
) : Authenticator {
    private val tag = this::class.simpleName

    override fun authenticate(route: Route?, response: Response): Request? {
        Log.i(tag, "Invoking authenticator")
        synchronized(this) {
            if (response.priorResponse != null) {
                // Already tried
                Log.w(tag, "Skipping authenticator for prior response")
                return null
            }

            val sessionRefreshed = runBlocking { authManager.refreshSession() }
            return if (sessionRefreshed) {
                val newToken = authManager.authState.value.currentAccessTokenOrNull()!!
                response.request.newBuilder()
                    .header(
                        name = HEADER_AUTHORIZATION_KEY,
                        value = "${HEADER_BEARER_PREFIX}$newToken"
                    ).build()
            } else {
                null
            }
        }
    }
}