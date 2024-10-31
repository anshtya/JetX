package com.anshtya.jetx.data.datastore

interface AuthTokenManager {
    suspend fun saveToken(accessToken: String, refreshToken: String)

    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?

    suspend fun deleteToken()
}