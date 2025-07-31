package com.anshtya.jetx.auth.ui.signup

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshtya.jetx.R
import com.anshtya.jetx.auth.ui.AuthUiState
import com.anshtya.jetx.auth.ui.components.AuthForm
import com.anshtya.jetx.common.ui.BackButton
import kotlinx.coroutines.launch

@Composable
fun SignUpRoute(
    onBackClick: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val signUpState by viewModel.uiState.collectAsStateWithLifecycle()

    SignUpScreen(
        uiState = signUpState,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onPasswordVisibilityChange = viewModel::onPasswordVisibilityChange,
        onErrorShown = viewModel::onErrorShown,
        onSignUpClick = viewModel::signUp,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignUpScreen(
    uiState: AuthUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: () -> Unit,
    onErrorShown: () -> Unit,
    onSignUpClick: () -> Unit,
    onBackClick: () -> Unit
) {
    BackHandler(uiState.isLoading) {}

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    uiState.errorMessage?.let {
        scope.launch { snackbarHostState.showSnackbar(it) }
        onErrorShown()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { BackButton(onClick = onBackClick) }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(40.dp))
            Text(
                text = stringResource(id = R.string.sign_up),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(40.dp))
            AuthForm(
                email = uiState.email,
                password = uiState.password,
                passwordVisible = uiState.passwordVisible,
                isLoading = uiState.isLoading,
                authButtonText = stringResource(id = R.string.sign_up),
                onEmailChange = onUsernameChange,
                onPasswordChange = onPasswordChange,
                onPasswordVisibilityChange = onPasswordVisibilityChange,
                onAuthButtonClick = onSignUpClick,
                emailError = uiState.emailError,
                passwordError = uiState.passwordError
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun SignUpScreenPreview() {
    SignUpScreen(
        uiState = AuthUiState(),
        onUsernameChange = {},
        onPasswordChange = {},
        onPasswordVisibilityChange = {},
        onErrorShown = {},
        onSignUpClick = {},
        onBackClick = {}
    )
}