package com.anshtya.jetx.ui.features.auth.signin

data class SignInUiState(
    val username: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val signInButtonEnabled: Boolean = true,
    val errorMessage: String? = null,
    val signInSuccessful: Boolean = false
)