package com.anshtya.jetx.core.network.auth

import android.util.Log
import com.anshtya.jetx.core.network.api.AuthApi
import com.anshtya.jetx.core.network.model.body.RefreshTokenBody
import com.anshtya.jetx.core.network.util.safeApiCall
import com.anshtya.jetx.core.network.util.toResult
import com.anshtya.jetx.core.preferences.TokenStore
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenProvider @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore
) {
    private val tag = this::class.simpleName

    fun getNewToken(refreshToken: String): Result<String> {
        return runBlocking {
            safeApiCall { authApi.refreshToken(RefreshTokenBody(refreshToken)) }.toResult()
        }.onSuccess {
            tokenStore.storeAuthToken(it.accessToken, it.refreshToken)
        }.onFailure {
            Log.e(tag, it.message, it)
        }.map { it.accessToken }
    }

    fun getStoredToken(key: String): String? {
        return tokenStore.getToken(key)
    }
}