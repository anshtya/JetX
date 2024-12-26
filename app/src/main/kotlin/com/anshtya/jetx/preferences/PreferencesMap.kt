package com.anshtya.jetx.preferences

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.anshtya.jetx.preferences.values.AuthValues

object PreferencesMap {
    private val map = mapOf<String, Preferences.Key<*>>(
        // Auth values
        AuthValues.PROFILE_CREATED to booleanPreferencesKey(AuthValues.PROFILE_CREATED),
    )

    @Suppress("UNCHECKED_CAST")
    fun <T> getPreferenceKey(key: String): Preferences.Key<T> =
        map[key] as? Preferences.Key<T> ?: throw IllegalArgumentException("Key doesn't exist.")
}