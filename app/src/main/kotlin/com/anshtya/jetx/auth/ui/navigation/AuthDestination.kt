package com.anshtya.jetx.auth.ui.navigation

import kotlinx.serialization.Serializable

sealed interface AuthDestination {
    @Serializable
    data object Entry : AuthDestination

    @Serializable
    data object SignIn : AuthDestination

    @Serializable
    data object SignUp : AuthDestination
}