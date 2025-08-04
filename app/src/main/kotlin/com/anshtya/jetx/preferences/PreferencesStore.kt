package com.anshtya.jetx.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.anshtya.jetx.preferences.model.AppUiProperties
import com.anshtya.jetx.preferences.model.ThemeOption
import com.anshtya.jetx.preferences.model.UserState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val PROFILE_CREATED = booleanPreferencesKey("profile_created")
        val THEME = stringPreferencesKey("theme")
    }

    val appUiProperties: Flow<AppUiProperties> = dataStore.data.map { preferences ->
        AppUiProperties(
            theme = preferences[THEME]?.let { enumValueOf<ThemeOption>(it) }
                ?: ThemeOption.SYSTEM_DEFAULT
        )
    }

    val userState: Flow<UserState> = dataStore.data.map { preferences ->
        UserState(
            profileCreated = preferences[PROFILE_CREATED]
        )
    }

    suspend fun setProfileCreated(profileCreated: Boolean) {
        dataStore.edit {
            it[PROFILE_CREATED] = profileCreated
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