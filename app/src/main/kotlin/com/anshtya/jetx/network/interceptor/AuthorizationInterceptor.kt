package com.anshtya.jetx.network.interceptor

import com.anshtya.jetx.auth.data.AuthTokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthorizationInterceptor @Inject constructor(
    private val authTokenManager: AuthTokenManager
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val accessToken = runBlocking { authTokenManager.getAccessToken()!! }
        val newRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        return chain.proceed(newRequest)
    }
}