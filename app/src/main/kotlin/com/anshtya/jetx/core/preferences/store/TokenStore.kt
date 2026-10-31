package com.anshtya.jetx.core.preferences.store

import android.content.SharedPreferences
import androidx.core.content.edit
import com.anshtya.jetx.core.preferences.model.AuthToken

class TokenStore(
    private val encryptedSharedPreferences: SharedPreferences
) {
    companion object {
        const val ACCESS_TOKEN = "access_token"
        const val REFRESH_TOKEN = "refresh_token"
    }

    /**
     * Returns the stored [AuthToken], or `null` if either token is missing.
     */
    fun getAuthToken(): AuthToken? {
        val accessToken = encryptedSharedPreferences.getString(ACCESS_TOKEN, null)
        val refreshToken = encryptedSharedPreferences.getString(REFRESH_TOKEN, null)
        if (accessToken == null || refreshToken == null) return null

        return AuthToken(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun storeAuthToken(
        access: String,
        refresh: String
    ) {
        encryptedSharedPreferences.edit {
            putString(ACCESS_TOKEN, access)
            putString(REFRESH_TOKEN, refresh)
        }
    }

    fun getAccessToken(): String? {
        return encryptedSharedPreferences.getString(ACCESS_TOKEN, null)
    }

    fun clear() {
        encryptedSharedPreferences.edit { clear() }
    }
}