package com.anshtya.jetx.profile.ui

import android.graphics.Bitmap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshtya.jetx.R
import com.anshtya.jetx.core.ui.components.scaffold.JetxScaffold
import com.anshtya.jetx.core.ui.components.textfield.UsernameTextField
import com.anshtya.jetx.core.ui.components.topappbar.JetxTopAppBar
import com.anshtya.jetx.core.ui.rememberMediaPicker
import com.anshtya.jetx.profile.ui.component.EditProfilePicture
import com.anshtya.jetx.ui.theme.JetXTheme
import com.anshtya.jetx.util.UriUtil.toBitmap
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object CreateProfileRoute

@Composable
fun CreateProfileRoute(
    viewModel: CreateProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateProfileScreen(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onUsernameChange = viewModel::onUsernameChange,
        onContinueClick = viewModel::createProfile,
        setProfilePicture = viewModel::setProfilePicture,
        onErrorShown = viewModel::onErrorShown
    )
}

@Composable
private fun CreateProfileScreen(
    uiState: CreateProfileUiState,
    onNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onContinueClick: () -> Unit,
    setProfilePicture: (Bitmap?) -> Unit,
    onErrorShown: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val pickMedia = rememberMediaPicker { uri ->
        if (uri != null) {
            scope.launch {
                setProfilePicture(uri.toBitmap(context))
            }
        }
    }

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
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) {
        LazyColumn (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp),
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.create_your_profile),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }
            item {
                EditProfilePicture(
                    model = uiState.profilePicture,
                    onRemovePhoto = { setProfilePicture(null) },
                    modifier = Modifier
                        .size(150.dp)
                        .clickable { pickMedia() }
                )
            }
            item {
                TextField(
                    value = uiState.name,
                    onValueChange = onNameChange,
                    enabled = !uiState.isLoading,
                    placeholder = {
                        Text(text = stringResource(id = R.string.name))
                    },
                    singleLine = true,
                    isError = uiState.nameError != null && !uiState.isLoading,
                    supportingText = {
                        Text(text = uiState.nameError ?: "")
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))

                var textFieldValueState by remember {
                    mutableStateOf(
                        TextFieldValue(
                            text = uiState.username,
                            selection = TextRange(uiState.username.length)
                        )
                    )
                }
                UsernameTextField(
                    username = textFieldValueState,
                    usernameError = uiState.usernameError,
                    usernameValid = uiState.usernameValid,
                    onUsernameChange = { value ->
                        textFieldValueState = value

                        if (value.text != uiState.username) {
                            onUsernameChange(value.text)
                        }
                    },
                    enabled = !uiState.isLoading,
                    isError = uiState.usernameError != null,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                if (uiState.isLoading) {
                    Box(Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                } else {
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            onContinueClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(text = stringResource(id = R.string.continue_text))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun CreateProfileScreenPreview() {
    JetXTheme {
        CreateProfileScreen(
            uiState = CreateProfileUiState(),
            onNameChange = {},
            onUsernameChange = {},
            onContinueClick = {},
            setProfilePicture = {},
            onErrorShown = {}
        )
    }
}