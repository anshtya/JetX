package com.anshtya.jetx.auth.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.anshtya.jetx.preferences.PreferencesMap
import com.anshtya.jetx.preferences.values.AuthValues
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthTokenStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AuthTokenManager {
    override val authenticated: Flow<Boolean> = dataStore.data
        .map { preferences ->
            val accessTokenKey = PreferencesMap.getPreferenceKey<String>(AuthValues.ACCESS_TOKEN)
            val refreshTokenKey = PreferencesMap.getPreferenceKey<String>(AuthValues.REFRESH_TOKEN)

            preferences[accessTokenKey] != null && preferences[refreshTokenKey] != null
        }

    override suspend fun saveToken(accessToken: String, refreshToken: String) {
        val accessTokenKey = PreferencesMap.getPreferenceKey<String>(AuthValues.ACCESS_TOKEN)
        val refreshTokenKey = PreferencesMap.getPreferenceKey<String>(AuthValues.REFRESH_TOKEN)

        dataStore.edit { preferences ->
            preferences[accessTokenKey] = accessToken
            preferences[refreshTokenKey] = refreshToken
        }
    }

    override suspend fun getAccessToken(): String? {
        val accessTokenKey = PreferencesMap.getPreferenceKey<String>(AuthValues.ACCESS_TOKEN)
        return dataStore.data
            .map { preferences -> preferences[accessTokenKey] }
            .first()
    }

    override suspend fun getRefreshToken(): String? {
        val refreshTokenKey = PreferencesMap.getPreferenceKey<String>(AuthValues.REFRESH_TOKEN)
        return dataStore.data
            .map { preferences -> preferences[refreshTokenKey] }
            .first()
    }

    override suspend fun deleteToken() {
        val accessTokenKey = PreferencesMap.getPreferenceKey<String>(AuthValues.ACCESS_TOKEN)
        val refreshTokenKey = PreferencesMap.getPreferenceKey<String>(AuthValues.REFRESH_TOKEN)

        dataStore.edit { preferences ->
            preferences.remove(accessTokenKey)
            preferences.remove(refreshTokenKey)
        }
    }
}