package com.anshtya.jetx.core.network.auth

import android.util.Log
import com.anshtya.jetx.core.network.util.HEADER_AUTHORIZATION_KEY
import com.anshtya.jetx.core.network.util.HEADER_BEARER_PREFIX
import com.anshtya.jetx.core.preferences.JetxPreferencesStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val store: JetxPreferencesStore
): Interceptor {
    private val tag = this::class.simpleName

    override fun intercept(chain: Interceptor.Chain): Response {
        Log.i(tag, "Invoking interceptor for ${chain.request().url}")
        val token = store.token.getAccessToken()
        val request = chain.request().newBuilder()
        request.addHeader(HEADER_AUTHORIZATION_KEY, "${HEADER_BEARER_PREFIX}$token")
        return chain.proceed(request.build())
    }
}