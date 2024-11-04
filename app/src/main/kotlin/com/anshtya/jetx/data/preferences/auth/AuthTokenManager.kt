package com.anshtya.jetx.data.preferences.auth

import kotlinx.coroutines.flow.Flow

interface AuthTokenManager {
    val authenticated: Flow<Boolean>

    suspend fun saveToken(accessToken: String, refreshToken: String)

    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?

    suspend fun deleteToken()
}