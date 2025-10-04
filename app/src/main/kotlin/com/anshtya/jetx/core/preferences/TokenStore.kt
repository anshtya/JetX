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
        const val USER_ID = "user_id"
    }

    fun storeAuthToken(
        userId: String,
        access: String,
        refresh: String
    ) {
        encryptedSharedPreferences.edit {
            putString(ACCESS_TOKEN, access)
            putString(REFRESH_TOKEN, refresh)
            putString(USER_ID, userId)
        }
    }

    fun getAuthToken(): AuthToken {
        return AuthToken(
            accessToken = encryptedSharedPreferences.getString(ACCESS_TOKEN, null),
            refreshToken = encryptedSharedPreferences.getString(REFRESH_TOKEN, null)
        )
    }

    fun getUserId(): String? {
        return getToken(USER_ID)
    }

    fun getToken(key: String): String? {
        return encryptedSharedPreferences.getString(key, null)
    }

    fun clearTokenStore() {
        encryptedSharedPreferences.edit { clear() }
    }
}