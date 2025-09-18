package com.anshtya.jetx.registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshtya.jetx.R
import com.anshtya.jetx.core.ui.DayNightPreview
import com.anshtya.jetx.core.ui.components.scaffold.JetxScaffold
import com.anshtya.jetx.core.ui.components.topappbar.JetxTopAppBar
import com.anshtya.jetx.ui.theme.JetXTheme
import com.anshtya.jetx.util.horizontalPadding
import com.anshtya.jetx.util.verticalPadding
import kotlinx.coroutines.launch

@Composable
fun EnterPhoneNumberRoute(
    onNavigateToSetupPin: () -> Unit,
    viewModel: RegistrationViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigateToNextScreen by viewModel.navigationEvent.collectAsStateWithLifecycle(false)

    EnterPhoneNumber(
        uiState = uiState,
        navigateToNextScreen = navigateToNextScreen,
        onNextClick = viewModel::onPhoneNumberConfirm,
        onCountryCodeChange = viewModel::onCountryCodeChange,
        onPhoneNumberChange = viewModel::onPhoneNumberChange,
        onNavigateToNextScreen = onNavigateToSetupPin,
        onErrorShown = viewModel::onErrorShown
    )
}

@Composable
private fun EnterPhoneNumber(
    uiState: RegistrationUiState,
    navigateToNextScreen: Boolean,
    onNextClick: () -> Unit,
    onCountryCodeChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onNavigateToNextScreen: () -> Unit,
    onErrorShown: () -> Unit,
) {
    LaunchedEffect(navigateToNextScreen) {
        if (navigateToNextScreen) onNavigateToNextScreen()
    }

    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }
    uiState.errorMessage?.let {
        scope.launch { snackbarHostState.showSnackbar(it) }
        onErrorShown()
    }

    val focusManager = LocalFocusManager.current

    JetxScaffold(
        topBar = {
            JetxTopAppBar()
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding)
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.phone_number),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.enter_phone_number),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    _root_ide_package_.com.anshtya.jetx.registration.components.CountryCodeDropdownMenu(
                        code = uiState.phoneCountryCode,
                        onCodeChange = onCountryCodeChange,
                        enabled = !uiState.isLoading,
                        modifier = Modifier.width(130.dp)
                    )
                    OutlinedTextField(
                        value = uiState.phoneNumber,
                        onValueChange = {
                            if (it.length <= 10) onPhoneNumberChange(it)
                        },
                        placeholder = {
                            Text(text = stringResource(id = R.string.phone_number))
                        },
                        enabled = !uiState.isLoading,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions { focusManager.clearFocus() },
                        modifier = Modifier.weight(1f)
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
                        onNextClick()
                    },
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
private fun EnterPhoneNumberScreenPreview() {
    JetXTheme {
        EnterPhoneNumber(
            uiState = RegistrationUiState(phoneCountryCode = "91"),
            navigateToNextScreen = false,
            onNextClick = {},
            onCountryCodeChange = {},
            onPhoneNumberChange = {},
            onNavigateToNextScreen = {},
            onErrorShown = {}
        )
    }
}