package com.anshtya.jetx.preferences

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.anshtya.jetx.preferences.values.ProfileValues

object PreferencesMap {
    private val map = mapOf<String, Preferences.Key<*>>(
        // Profile values
        ProfileValues.PROFILE_CREATED to booleanPreferencesKey(ProfileValues.PROFILE_CREATED),
        ProfileValues.USER_ID to stringPreferencesKey(ProfileValues.USER_ID),
    )

    @Suppress("UNCHECKED_CAST")
    fun <T> getPreferenceKey(key: String): Preferences.Key<T> =
        map[key] as? Preferences.Key<T> ?: throw IllegalArgumentException("$key doesn't exist.")
}