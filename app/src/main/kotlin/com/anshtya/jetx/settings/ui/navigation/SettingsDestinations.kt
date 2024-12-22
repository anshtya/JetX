package com.anshtya.jetx.settings.ui.navigation

import kotlinx.serialization.Serializable

sealed interface SettingsDestinations {
    @Serializable
    data object SettingList : SettingsDestinations
}