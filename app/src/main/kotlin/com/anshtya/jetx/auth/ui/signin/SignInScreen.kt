package com.anshtya.jetx.auth.ui.signin

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
import androidx.compose.runtime.LaunchedEffect
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
import com.anshtya.jetx.auth.ui.components.AuthForm
import com.anshtya.jetx.common.ui.BackButton
import kotlinx.coroutines.launch

@Composable
fun SignInRoute(
    onNavigateToHome: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SignInScreen(
        uiState = uiState,
        onUsernameChange = viewModel::changeUsername,
        onPasswordChange = viewModel::changePassword,
        onPasswordVisibilityChange = viewModel::changePasswordVisibility,
        onErrorShown = viewModel::errorShown,
        onSignInClick = viewModel::signIn,
        onSignInSuccessful = onNavigateToHome,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignInScreen(
    uiState: SignInUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: () -> Unit,
    onErrorShown: () -> Unit,
    onSignInClick: () -> Unit,
    onSignInSuccessful: () -> Unit,
    onBackClick: () -> Unit
) {
    LaunchedEffect(uiState.signInSuccessful) {
        if (uiState.signInSuccessful) {
            onSignInSuccessful()
        }
    }

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
                text = stringResource(id = R.string.sign_in),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(40.dp))
            AuthForm(
                username = uiState.username,
                password = uiState.password,
                passwordVisible = uiState.passwordVisible,
                authButtonEnabled = uiState.signInButtonEnabled,
                authButtonText = stringResource(id = R.string.sign_in),
                onUsernameChange = onUsernameChange,
                onPasswordChange = onPasswordChange,
                onPasswordVisibilityChange = onPasswordVisibilityChange,
                onAuthButtonClick = onSignInClick
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun SignInScreenPreview() {
    SignInScreen(
        uiState = SignInUiState(),
        onUsernameChange = {},
        onPasswordChange = {},
        onPasswordVisibilityChange = {},
        onErrorShown = {},
        onSignInClick = {},
        onSignInSuccessful = {},
        onBackClick = {}
    )
}