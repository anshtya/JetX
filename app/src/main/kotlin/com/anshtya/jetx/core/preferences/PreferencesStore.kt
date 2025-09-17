package com.anshtya.jetx.core.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.anshtya.jetx.core.preferences.model.AppUiProperties
import com.anshtya.jetx.core.preferences.model.ThemeOption
import com.anshtya.jetx.core.preferences.model.UserState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PreferencesStore(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val PROFILE_CREATED = booleanPreferencesKey("profile_created")
        val ONBOARDED = booleanPreferencesKey("onboarded")
        val THEME = stringPreferencesKey("theme")
    }

    val appUiProperties: Flow<AppUiProperties> = dataStore.data.map { preferences ->
        AppUiProperties(
            theme = preferences[THEME]?.let { enumValueOf<ThemeOption>(it) }
                ?: ThemeOption.SYSTEM_DEFAULT
        )
    }.distinctUntilChanged()

    val userState: Flow<UserState> = dataStore.data.map { preferences ->
        UserState(
            profileCreated = preferences[PROFILE_CREATED] ?: false,
            onboardingCompleted = preferences[ONBOARDED] ?: false
        )
    }.distinctUntilChanged()

    suspend fun setProfileCreated() {
        dataStore.edit {
            it[PROFILE_CREATED] = true
        }
    }

    suspend fun setOnboarded() {
        dataStore.edit {
            it[ONBOARDED] = true
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