package com.anshtya.jetx.auth.ui

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val authButtonEnabled: Boolean = true,
    val emailError: String? = null,
    val passwordError: String? = null,
    val errorMessage: String? = null
)
