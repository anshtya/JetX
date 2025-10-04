package com.anshtya.jetx.registration

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshtya.jetx.R
import com.anshtya.jetx.core.ui.DayNightPreview
import com.anshtya.jetx.core.ui.components.button.BackButton
import com.anshtya.jetx.core.ui.components.scaffold.JetxScaffold
import com.anshtya.jetx.core.ui.components.topappbar.JetxTopAppBar
import com.anshtya.jetx.ui.theme.JetXTheme
import com.anshtya.jetx.util.horizontalPadding
import com.anshtya.jetx.util.verticalPadding
import kotlinx.coroutines.launch

@Composable
fun SetupPinRoute(
    onNavigateUp: () -> Unit,
    viewModel: RegistrationViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SetupPinScreen(
        uiState = uiState,
        userExists = viewModel.userExists,
        onBackClick = onNavigateUp,
        onNextClick = viewModel::authUser,
        onErrorShown = viewModel::onErrorShown
    )
}

@Composable
private fun SetupPinScreen(
    uiState: RegistrationUiState,
    userExists: Boolean,
    onBackClick: () -> Unit,
    onNextClick: (String) -> Unit,
    onErrorShown: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var pin by rememberSaveable { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    uiState.errorMessage?.let {
        scope.launch { snackbarHostState.showSnackbar(it) }
        onErrorShown()
    }

    JetxScaffold(
        topBar = {
            JetxTopAppBar(
                navigationIcon = { BackButton(onBackClick) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding)
        ) {
            var passwordVisible by rememberSaveable { mutableStateOf(false) }

            Column(Modifier.weight(1f)) {
                Text(
                    text = if (userExists) {
                        stringResource(id = R.string.enter_pin)
                    } else {
                        stringResource(id = R.string.create_pin)
                    },
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (userExists) {
                        stringResource(id = R.string.enter_pin_text)
                    } else {
                        stringResource(id = R.string.create_pin_text)
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(20.dp))
                Box(Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = pin,
                        onValueChange = {
                            if (it.length <= 4) pin = it
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible }
                            ) {
                                if (passwordVisible) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = stringResource(id = R.string.show_password),
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.VisibilityOff,
                                        contentDescription = stringResource(id = R.string.hide_password),
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions { focusManager.clearFocus() },
                        modifier = Modifier
                            .width(180.dp)
                            .align(Alignment.Center)
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            if (uiState.isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.End))
            } else {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onNextClick(pin)
                    },
                    enabled = pin.isNotEmpty() && pin.length == 4,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = stringResource(id = R.string.next)
                    )
                }
            }
        }
    }
}

@DayNightPreview
@Composable
private fun EnterPinScreenPreview() {
    JetXTheme {
        SetupPinScreen(
            uiState = RegistrationUiState(phoneCountryCode = "91"),
            userExists = false,
            onBackClick = {},
            onNextClick = {},
            onErrorShown = {}
        )
    }
}

@Preview(name = "Create PIN")
@Composable
private fun CreatePinScreenPreview() {
    JetXTheme {
        SetupPinScreen(
            uiState = RegistrationUiState(phoneCountryCode = "91"),
            userExists = true,
            onBackClick = {},
            onNextClick = {},
            onErrorShown = {}
        )
    }
}