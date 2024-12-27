package com.anshtya.jetx.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.String

class PreferencesStoreImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
): PreferencesStore {
    override val data = dataStore.data

    override suspend fun <T> get(key: String): T? {
        val preferenceKey = PreferencesMap.getPreferenceKey<T>(key)
        return get(preferenceKey)
    }

    override suspend fun <T> get(key: Preferences.Key<T>): T? {
        return dataStore.data
            .map { preferences -> preferences[key] }
            .first()
    }

    override suspend fun <T> set(key: String, value: T) {
        val preferencesKey = PreferencesMap.getPreferenceKey<T>(key)
        set(preferencesKey, value)
    }

    override suspend fun <T> set(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences -> preferences[key] = value }
    }
}