package com.anshtya.jetx.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface TokenManager {
    suspend fun saveToken(token: String)

    suspend fun getToken(): String?

    suspend fun deleteToken()
}

class TokenDatastore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : TokenManager {
    companion object {
        val USER_TOKEN = stringPreferencesKey("user_token")
    }

    override suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }

    override suspend fun getToken(): String? {
        return dataStore.data
            .map { preferences -> preferences[USER_TOKEN] }
            .first()
    }

    override suspend fun deleteToken() {
        dataStore.edit { preferences ->
            preferences.remove(USER_TOKEN)
        }
    }
}