package com.anshtya.jetx.settings.data

import com.anshtya.jetx.preferences.PreferencesStore
import com.anshtya.jetx.preferences.values.SettingsValues.THEME
import com.anshtya.jetx.settings.data.model.ThemeOption
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val preferencesStore: PreferencesStore
): SettingsRepository {
    override suspend fun setTheme(option: ThemeOption) {
        preferencesStore.setString(THEME, option.name)
    }
}