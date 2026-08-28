package com.anshtya.jetx.settings.data

import com.anshtya.jetx.core.preferences.model.ThemeOption
import com.anshtya.jetx.core.preferences.model.UserState
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val theme: Flow<ThemeOption>

    val userState: Flow<UserState>

    suspend fun setTheme(theme: ThemeOption)

    suspend fun setOnboarded()
}
