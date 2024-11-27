package com.anshtya.jetx.auth.ui.signup

data class SignUpUiState(
    val username: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val signUpButtonEnabled: Boolean = true,
    val emailError: String? = null,
    val passwordError: String? = null,
    val errorMessage: String? = null,
    val signUpSuccessful: Boolean = false
)
