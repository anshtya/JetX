package com.anshtya.jetx.auth.ui.navigation

import kotlinx.serialization.Serializable

sealed interface AuthDestinations {
    @Serializable
    data object Entry : AuthDestinations

    @Serializable
    data object SignIn : AuthDestinations

    @Serializable
    data object SignUp : AuthDestinations

    @Serializable
    data object CreateProfile : AuthDestinations
}