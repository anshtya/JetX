package com.anshtya.jetx.settings.ui.navigation

import kotlinx.serialization.Serializable

sealed interface SettingsDestination {
    @Serializable
    data object SettingList : SettingsDestination
}