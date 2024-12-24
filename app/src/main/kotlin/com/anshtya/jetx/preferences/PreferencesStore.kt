package com.anshtya.jetx.preferences

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface PreferencesStore {
    val data: Flow<Preferences>

    suspend fun <T> get(key: String): T?

    suspend fun <T> set(key: String, value: T)
}