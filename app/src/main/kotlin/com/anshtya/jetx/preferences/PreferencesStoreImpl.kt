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
        return dataStore.data
            .map { preferences -> preferences[preferenceKey] }
            .first()
    }

    override suspend fun <T> set(key: String, value: T) {
        val preferencesKey = PreferencesMap.getPreferenceKey<T>(key)
        dataStore.edit { preferences -> preferences[preferencesKey] = value }
    }
}