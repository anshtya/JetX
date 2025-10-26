package com.anshtya.jetx.registration.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface RegistrationDestination {
    @Serializable
    data object EnterPhoneNumber : RegistrationDestination

    @Serializable
    data object SetupPin : RegistrationDestination
}