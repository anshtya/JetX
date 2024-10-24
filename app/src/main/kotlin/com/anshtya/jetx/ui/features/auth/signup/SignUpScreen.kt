package com.anshtya.jetx.ui.features.auth.signup

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SignUpRoute(
    viewModel: SignUpViewModel = hiltViewModel()
) {
    SignUpScreen()
}

@Composable
private fun SignUpScreen() {
}