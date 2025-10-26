package com.anshtya.jetx.settings.profile.editname

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshtya.jetx.R
import com.anshtya.jetx.core.ui.components.button.BackButton
import com.anshtya.jetx.core.ui.components.scaffold.JetxScaffold
import com.anshtya.jetx.core.ui.components.topappbar.JetxTopAppBar
import com.anshtya.jetx.util.horizontalPadding
import com.anshtya.jetx.util.verticalPadding
import kotlinx.coroutines.launch

@Composable
fun EditNameRoute(
    onNavigateUp: () -> Unit,
    viewModel: EditNameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val previousName = viewModel.previousName

    EditNameScreen(
        uiState = uiState,
        previousName = previousName,
        onNameChange = viewModel::onNameChange,
        onSaveName = viewModel::onSaveName,
        onErrorShown = viewModel::onErrorShown,
        onBackClick = onNavigateUp
    )
}

@Composable
private fun EditNameScreen(
    uiState: EditNameUiState,
    previousName: String,
    onNameChange: (String) -> Unit,
    onSaveName: () -> Unit,
    onErrorShown: () -> Unit,
    onBackClick: () -> Unit
) {
    LaunchedEffect(uiState.nameSaved) {
        if (uiState.nameSaved) {
            onBackClick()
        }
    }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }
    uiState.errorMessage?.let {
        scope.launch { snackbarHostState.showSnackbar(it) }
        onErrorShown()
    }

    JetxScaffold(
        topBar = {
            JetxTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.edit_name))
                },
                navigationIcon = {
                    BackButton { onBackClick() }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding)
        ) {
            var textFieldValueState by remember {
                mutableStateOf(
                    TextFieldValue(
                        text = uiState.name,
                        selection = TextRange(uiState.name.length)
                    )
                )
            }

            TextField(
                value = textFieldValueState,
                onValueChange = { value ->
                    textFieldValueState = value

                    if (value.text != uiState.name) {
                        onNameChange(value.text)
                    }
                },
                enabled = !uiState.isLoading,
                placeholder = {
                    Text(text = stringResource(id = R.string.name))
                },
                singleLine = true,
                isError = uiState.nameError != null && !uiState.isLoading,
                supportingText = {
                    Text(text = uiState.nameError ?: "")
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.End))
            } else {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onSaveName()
                    },
                    enabled = uiState.name != previousName,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = stringResource(id = R.string.save))
                }
            }
        }
    }
}