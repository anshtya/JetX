package com.anshtya.jetx.settings.navigation

import kotlinx.serialization.Serializable

sealed interface SettingsDestination {
    @Serializable
    data object SettingList : SettingsDestination

    @Serializable
    data object UserProfileGraph : SettingsDestination

    sealed interface UserProfile : SettingsDestination {
        @Serializable
        data object View : UserProfile

        @Serializable
        data class EditName(val name: String) : UserProfile

        @Serializable
        data class EditUsername(val username: String) : UserProfile
    }
}