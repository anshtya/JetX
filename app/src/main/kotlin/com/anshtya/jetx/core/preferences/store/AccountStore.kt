package com.anshtya.jetx.core.preferences.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AccountStore(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val FCM_TOKEN = stringPreferencesKey("fcm_token")
        val USER_ID = stringPreferencesKey("user_id")
    }

    suspend fun getFcmToken(): String? {
        return dataStore.data.map { it[FCM_TOKEN] }.first()
    }

    suspend fun storeFcmToken(fcmToken: String) {
        dataStore.edit { it[FCM_TOKEN] = fcmToken }
    }

    suspend fun getUserId(): String? {
        return dataStore.data.map { it[USER_ID] }.first()
    }

    suspend fun storeUserId(userId: String) {
        dataStore.edit { it[USER_ID] = userId }
    }

    suspend fun clear() {
        dataStore.edit {
            it.remove(FCM_TOKEN)
            it.remove(USER_ID)
        }
    }
}