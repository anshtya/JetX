package com.anshtya.jetx.settings.profile

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshtya.jetx.R
import com.anshtya.jetx.core.model.UserProfile
import com.anshtya.jetx.core.model.sampledata.sampleUsers
import com.anshtya.jetx.core.ui.DayNightPreview
import com.anshtya.jetx.core.ui.ProfilePicture
import com.anshtya.jetx.core.ui.components.button.BackButton
import com.anshtya.jetx.core.ui.components.scaffold.JetxScaffold
import com.anshtya.jetx.core.ui.components.topappbar.JetxTopAppBar
import com.anshtya.jetx.core.ui.rememberMediaPicker
import com.anshtya.jetx.settings.SettingsViewModel
import com.anshtya.jetx.settings.components.SettingsItem
import com.anshtya.jetx.ui.theme.JetXTheme
import kotlinx.coroutines.launch

@Composable
fun ViewProfileRoute(
    onNavigateUp: () -> Unit,
    onNavigateToEditName: (String) -> Unit,
    onNavigateToEditUsername: (String) -> Unit,
    settingsViewModel: SettingsViewModel,
    viewModel: ViewProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userProfile by settingsViewModel.userProfile.collectAsStateWithLifecycle()

    ViewProfileScreen(
        uiState = uiState,
        userProfile = userProfile,
        onEditProfilePhoto = viewModel::onEditProfilePhoto,
        onEditNameClick = onNavigateToEditName,
        onEditUsernameClick = onNavigateToEditUsername,
        onErrorShown = viewModel::onErrorShown,
        onBackClick = onNavigateUp,
    )
}

@Composable
private fun ViewProfileScreen(
    uiState: ViewProfileUiState,
    userProfile: UserProfile?,
    onEditProfilePhoto: (Uri?) -> Unit,
    onEditNameClick: (String) -> Unit,
    onEditUsernameClick: (String) -> Unit,
    onErrorShown: () -> Unit,
    onBackClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val pickMedia = rememberMediaPicker { uri ->
        if (uri != null) {
            scope.launch {
                onEditProfilePhoto(uri)
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    uiState.errorMessage?.let {
        scope.launch { snackbarHostState.showSnackbar(it) }
        onErrorShown()
    }

    JetxScaffold(
        topBar = {
            JetxTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.profile))
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
        if (userProfile != null) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        Box(Modifier.size(100.dp)) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(Modifier.align(Alignment.Center))
                            } else {
                                ProfilePicture(model = userProfile.pictureUrl?.toUri())
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Button(
                                onClick = { pickMedia() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                )
                            ) {
                                Text(
                                    text = stringResource(id = R.string.edit_photo),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            userProfile.pictureUrl?.let {
                                Button(
                                    onClick = { onEditProfilePhoto(null) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                    )
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.delete_photo),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    SettingsItem(
                        text = userProfile.name,
                        icon = Icons.Outlined.Person,
                        onClick = { onEditNameClick(userProfile.name) }
                    )
                }
                item {
                    SettingsItem(
                        text = userProfile.username,
                        icon = Icons.Default.AlternateEmail,
                        onClick = { onEditUsernameClick(userProfile.username) }
                    )
                }
            }
        }
    }
}

@DayNightPreview
@Composable
private fun ViewProfileScreenPreview() {
    JetXTheme {
        ViewProfileScreen(
            uiState = ViewProfileUiState(),
            userProfile = sampleUsers[0],
            onEditProfilePhoto = {},
            onEditNameClick = {},
            onEditUsernameClick = {},
            onErrorShown = {},
            onBackClick = {}
        )
    }
}