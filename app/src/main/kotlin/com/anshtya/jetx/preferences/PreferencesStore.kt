package com.anshtya.jetx.preferences

import kotlinx.coroutines.flow.Flow

interface PreferencesStore {
    suspend fun getBoolean(key: String): Boolean?

    fun getBooleanFlow(key: String): Flow<Boolean?>

    suspend fun setBoolean(key: String, value: Boolean)

    suspend fun getString(key: String): String?

    fun getStringFlow(key: String): Flow<String?>

    suspend fun setString(key: String, value: String)

    suspend fun clearPreferences()
}