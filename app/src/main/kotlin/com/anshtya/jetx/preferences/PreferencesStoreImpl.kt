package com.anshtya.jetx.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesStoreImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesStore {
    override suspend fun getBoolean(key: String): Boolean? {
        val preferenceKey = PreferencesMap.getPreferenceKey<Boolean>(key)
        val data = dataStore.data.first()
        return data[preferenceKey]
    }

    override fun getBooleanFlow(key: String): Flow<Boolean?> {
        val preferenceKey = PreferencesMap.getPreferenceKey<Boolean>(key)
        return dataStore.data.map { preferences ->
            preferences[preferenceKey]
        }
    }

    override suspend fun setBoolean(key: String, value: Boolean) {
        val preferenceKey = PreferencesMap.getPreferenceKey<Boolean>(key)
        dataStore.edit { preferences ->
            preferences[preferenceKey] = value
        }
    }

    override suspend fun getString(key: String): String? {
        val preferenceKey = PreferencesMap.getPreferenceKey<String>(key)
        val data = dataStore.data.first()
        return data[preferenceKey]
    }

    override fun getStringFlow(key: String): Flow<String?> {
        val preferenceKey = PreferencesMap.getPreferenceKey<String>(key)
        return dataStore.data.map { preferences ->
            preferences[preferenceKey]
        }
    }

    override suspend fun setString(key: String, value: String) {
        val preferenceKey = PreferencesMap.getPreferenceKey<String>(key)
        dataStore.edit { preferences ->
            preferences[preferenceKey] = value
        }
    }

    override suspend fun clearPreferences() {
        dataStore.edit {
            it.clear()
        }
    }

}