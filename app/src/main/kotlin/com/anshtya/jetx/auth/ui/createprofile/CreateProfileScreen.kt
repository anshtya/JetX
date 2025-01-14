package com.anshtya.jetx.auth.ui.createprofile

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshtya.jetx.R
import com.anshtya.jetx.common.ui.BackButton
import com.anshtya.jetx.common.ui.ComponentPreview
import com.anshtya.jetx.common.ui.ProfilePicture
import com.anshtya.jetx.common.ui.rememberMediaPicker
import com.anshtya.jetx.util.toBitmap
import kotlinx.coroutines.launch

@Composable
fun CreateProfileRoute(
    onNavigateToHome: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: CreateProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateProfileScreen(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onUsernameChange = viewModel::onUsernameChange,
        onContinueClick = viewModel::createProfile,
        onProfileCreated = onNavigateToHome,
        onBackClick = onNavigateUp,
        setProfilePicture = viewModel::setProfilePicture,
        onErrorShown = viewModel::onErrorShown
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateProfileScreen(
    uiState: CreateProfileUiState,
    onNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onContinueClick: () -> Unit,
    onProfileCreated: () -> Unit,
    onBackClick: () -> Unit,
    setProfilePicture: (Bitmap) -> Unit,
    onErrorShown: () -> Unit
) {
    LaunchedEffect(uiState.profileCreated) {
        if (uiState.profileCreated) onProfileCreated()
    }

    var selectedProfilePicture by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val pickMedia = rememberMediaPicker { uri ->
        if (uri != null) {
            selectedProfilePicture = uri
        }
    }
    LaunchedEffect(selectedProfilePicture) {
        if (selectedProfilePicture != null) {
            setProfilePicture(selectedProfilePicture!!.toBitmap(context))
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
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = stringResource(id = R.string.create_your_profile),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(30.dp))
            ProfilePicture(
                model = uiState.profilePicture,
                onClick = pickMedia,
                modifier = Modifier.size(100.dp)
            )
            Spacer(Modifier.height(30.dp))
            TextField(
                value = uiState.name,
                onValueChange = onNameChange,
                enabled = uiState.continueButtonEnabled,
                placeholder = {
                    Text(text = stringResource(id = R.string.name))
                },
                singleLine = true,
                isError = uiState.nameError != null && uiState.continueButtonEnabled,
                supportingText = {
                    Text(text = uiState.nameError ?: "")
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            TextField(
                value = uiState.username,
                onValueChange = onUsernameChange,
                enabled = uiState.continueButtonEnabled,
                placeholder = {
                    Text(text = stringResource(id = R.string.username))
                },
                singleLine = true,
                isError = uiState.usernameError != null && uiState.continueButtonEnabled,
                supportingText = {
                    Text(text = uiState.usernameError ?: "")
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onContinueClick,
                enabled = uiState.continueButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(text = stringResource(id = R.string.continue_text))
            }
        }
    }
}

@Preview
@Composable
private fun CreateProfileScreenPreview() {
    ComponentPreview {
        CreateProfileScreen(
            uiState = CreateProfileUiState(),
            onNameChange = {},
            onUsernameChange = {},
            onContinueClick = {},
            onProfileCreated = {},
            onBackClick = {},
            setProfilePicture = {},
            onErrorShown = {}
        )
    }
}