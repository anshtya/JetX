package com.anshtya.jetx.settings.data

import com.anshtya.jetx.settings.data.model.ThemeOption

interface SettingsRepository {
    suspend fun setTheme(option: ThemeOption)
}