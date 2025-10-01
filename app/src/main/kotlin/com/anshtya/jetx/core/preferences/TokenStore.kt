package com.anshtya.jetx.core.preferences

import android.content.SharedPreferences
import androidx.core.content.edit
import com.anshtya.jetx.core.preferences.model.AuthToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenStore @Inject constructor(
    private val encryptedSharedPreferences: SharedPreferences
) {
    companion object {
        const val ACCESS_TOKEN = "access_token"
        const val REFRESH_TOKEN = "refresh_token"
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

    fun getAuthToken(): AuthToken {
        return AuthToken(
            accessToken = encryptedSharedPreferences.getString(ACCESS_TOKEN, null),
            refreshToken = encryptedSharedPreferences.getString(REFRESH_TOKEN, null)
        )
    }

    fun getAccessToken(): String? {
        return getToken(ACCESS_TOKEN)
    }

    fun getToken(key: String): String? {
        return encryptedSharedPreferences.getString(key, null)
    }

    fun clearTokenStore() {
        encryptedSharedPreferences.edit { clear() }
    }
}