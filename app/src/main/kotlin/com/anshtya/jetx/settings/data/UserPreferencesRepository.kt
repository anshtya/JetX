package com.anshtya.jetx.settings.data

import com.anshtya.jetx.core.preferences.model.ThemeOption
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val theme: Flow<ThemeOption>

    suspend fun setTheme(theme: ThemeOption)
}
