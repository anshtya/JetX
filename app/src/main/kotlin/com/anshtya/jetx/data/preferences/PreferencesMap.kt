package com.anshtya.jetx.data.preferences

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.anshtya.jetx.data.preferences.values.AuthValues
import kotlin.String

object PreferencesMap {
    val map = mapOf<String, Preferences.Key<*>>(
        // Auth values
        AuthValues.ACCESS_TOKEN to stringPreferencesKey(AuthValues.ACCESS_TOKEN),
        AuthValues.REFRESH_TOKEN to stringPreferencesKey(AuthValues.REFRESH_TOKEN)
    )

    @Suppress("UNCHECKED_CAST")
    fun <T> getPreferenceKey(key: String): Preferences.Key<T> =
        map[key] as? Preferences.Key<T> ?: throw IllegalArgumentException("Key doesn't exist.")
}