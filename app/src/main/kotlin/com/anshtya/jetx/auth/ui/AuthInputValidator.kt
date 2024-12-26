package com.anshtya.jetx.auth.ui

import android.util.Patterns

object AuthInputValidator {
    const val EMAIL_ERROR = "emailError"
    const val PASSWORD_ERROR = "passwordError"

    fun validateAuthInputs(
        email: String,
        password: String,
        setErrors: (MutableMap<String, String?>) -> Unit
    ): Boolean {
        val errors = mutableMapOf<String, String?>()

        if (email.isEmpty()) {
            errors[EMAIL_ERROR] = "Email should not be empty"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errors[EMAIL_ERROR] = "Invalid email format"
        }

        if (password.isEmpty()) {
            errors[PASSWORD_ERROR] = "Password should not be empty"
        } else if (password.length < 8) {
            errors[PASSWORD_ERROR] = "Password should be of minimum 8 characters"
        }

        return if (errors.isNotEmpty()) {
            setErrors(errors)
            false
        } else {
            true
        }
    }
}
