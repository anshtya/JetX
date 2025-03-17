package com.anshtya.jetx.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.anshtya.jetx.common.model.ProfileData
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val PROFILE_CREATED = booleanPreferencesKey("profile_created")
        val USER_ID = stringPreferencesKey("user_id")

        val THEME = stringPreferencesKey("theme")
    }

    val profileFlow = dataStore.data.map {
        ProfileData(
            profileCreated = it[PROFILE_CREATED] ?: false,
            userId = it[USER_ID]
        )
    }
    val themeFlow = dataStore.data.map { it[THEME] }

    suspend fun setProfile(
        profileCreated: Boolean,
        userId: String
    ) {
        dataStore.edit {
            it[PROFILE_CREATED] = profileCreated
            it[USER_ID] = userId
        }
    }

    suspend fun setTheme(theme: String) {
        dataStore.edit {
            it[THEME] = theme
        }
    }

    suspend fun clearPreferences() {
        dataStore.edit { it.clear() }
    }
}