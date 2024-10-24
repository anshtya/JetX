package com.anshtya.jetx.ui.features.auth.signin

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SignInRoute(
    onSignUpClick: () -> Unit,
    viewModel: SignInViewModel = hiltViewModel()
) {
    SignInScreen(onSignUpClick = onSignUpClick)
}

@Composable
private fun SignInScreen(
    onSignUpClick: () -> Unit
) {

}