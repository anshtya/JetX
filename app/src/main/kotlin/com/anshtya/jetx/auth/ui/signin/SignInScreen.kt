package com.anshtya.jetx.auth.ui.signin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.anshtya.jetx.core.ui.components.button.BackButton
import com.anshtya.jetx.core.ui.components.scaffold.JetxScaffold
import com.anshtya.jetx.core.ui.components.topappbar.JetxTopAppBar
import kotlinx.coroutines.launch

@Composable
fun SignInRoute(
    onBackClick: () -> Unit,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SignInScreen(
        uiState = uiState,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onPasswordVisibilityChange = viewModel::onPasswordVisibilityChange,
        onErrorShown = viewModel::onErrorShown,
        onSignInClick = viewModel::signIn,
        onBackClick = onBackClick
    )
}

@Composable
private fun SignInScreen(
    uiState: AuthUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: () -> Unit,
    onErrorShown: () -> Unit,
    onSignInClick: () -> Unit,
    onBackClick: () -> Unit
) {
    BackHandler(uiState.isLoading) {  }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    uiState.errorMessage?.let {
        scope.launch { snackbarHostState.showSnackbar(it) }
        onErrorShown()
    }

    JetxScaffold(
        topBar = {
            JetxTopAppBar(
                title = {},
                navigationIcon = { BackButton(onClick = onBackClick) }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxSize()
                .imePadding()
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
                email = uiState.email,
                password = uiState.password,
                passwordVisible = uiState.passwordVisible,
                isLoading = uiState.isLoading,
                authButtonText = stringResource(id = R.string.sign_in),
                onEmailChange = onUsernameChange,
                onPasswordChange = onPasswordChange,
                onPasswordVisibilityChange = onPasswordVisibilityChange,
                onAuthButtonClick = onSignInClick,
                emailError = uiState.emailError,
                passwordError = uiState.passwordError
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun SignInScreenPreview() {
    SignInScreen(
        uiState = AuthUiState(),
        onUsernameChange = {},
        onPasswordChange = {},
        onPasswordVisibilityChange = {},
        onErrorShown = {},
        onSignInClick = {},
        onBackClick = {}
    )
}