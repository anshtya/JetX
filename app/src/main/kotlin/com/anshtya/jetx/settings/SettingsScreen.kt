package com.anshtya.jetx.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.rounded.Brightness4
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshtya.jetx.R
import com.anshtya.jetx.core.model.UserProfile
import com.anshtya.jetx.core.model.sampledata.sampleUsers
import com.anshtya.jetx.core.preferences.model.ThemeOption
import com.anshtya.jetx.core.ui.DayNightPreview
import com.anshtya.jetx.core.ui.ProfilePicture
import com.anshtya.jetx.core.ui.components.button.BackButton
import com.anshtya.jetx.core.ui.components.scaffold.JetxScaffold
import com.anshtya.jetx.core.ui.components.topappbar.JetxTopAppBar
import com.anshtya.jetx.settings.components.SettingsItem
import com.anshtya.jetx.ui.theme.JetXTheme
import com.anshtya.jetx.util.horizontalPadding
import com.anshtya.jetx.util.verticalPadding

@Composable
fun SettingsRoute(
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: SettingsViewModel
) {
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle(null)

    SettingsScreen(
        userSettings = userSettings,
        userProfile = userProfile,
        errorMessage = errorMessage,
        onSignOutClick = viewModel::onSignOutClick,
        onThemeChange = viewModel::changeTheme,
        onProfileClick = onProfileClick,
        onBackClick = onBackClick
    )
}

@Composable
private fun SettingsScreen(
    userSettings: UserSettings?,
    userProfile: UserProfile?,
    errorMessage: String?,
    onThemeChange: (ThemeOption) -> Unit,
    onProfileClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    var showThemeDialog by rememberSaveable { mutableStateOf(false) }
    if (showThemeDialog) {
        ThemeSelectDialog(
            currentTheme = userSettings!!.theme,
            onDismissDialog = { showThemeDialog = false },
            onThemeChange = onThemeChange
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    JetxScaffold(
        topBar = {
            JetxTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.settings))
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
        if (userSettings != null) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                userProfile?.let {
                    item {
                        ProfileItem(
                            userProfile = it,
                            onItemClick = onProfileClick
                        )
                    }
                }
                item {
                    SettingsItem(
                        text = stringResource(id = R.string.theme),
                        secondaryText = userSettings.theme.displayName,
                        icon = Icons.Rounded.Brightness4,
                        onClick = { showThemeDialog = true }
                    )
                }
                item {
                    SettingsItem(
                        text = stringResource(id = R.string.sign_out),
                        icon = Icons.AutoMirrored.Filled.Logout,
                        onClick = onSignOutClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileItem(
    userProfile: UserProfile,
    onItemClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(
                horizontal = horizontalPadding,
                vertical = verticalPadding
            )
    ) {
        ProfilePicture(
            model = userProfile.pictureUrl?.toUri(),
            modifier = Modifier
                .size(120.dp)
                .padding(10.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = userProfile.name,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = userProfile.phoneNumber,
                color = Color.Gray
            )
            Text(
                text = userProfile.username,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ThemeSelectDialog(
    currentTheme: ThemeOption,
    onThemeChange: (ThemeOption) -> Unit,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var chosenTheme by remember { mutableStateOf(currentTheme) }

    AlertDialog(
        onDismissRequest = onDismissDialog,
        title = {
            Text(text = stringResource(id = R.string.theme))
        },
        text = {
            Column(Modifier.selectableGroup()) {
                SettingsDialogChooserRow(
                    text = ThemeOption.SYSTEM_DEFAULT.displayName,
                    selected = chosenTheme == ThemeOption.SYSTEM_DEFAULT,
                    onClick = { chosenTheme = ThemeOption.SYSTEM_DEFAULT }
                )
                SettingsDialogChooserRow(
                    text = ThemeOption.LIGHT.displayName,
                    selected = chosenTheme == ThemeOption.LIGHT,
                    onClick = { chosenTheme = ThemeOption.LIGHT }
                )
                SettingsDialogChooserRow(
                    text = ThemeOption.DARK.displayName,
                    selected = chosenTheme == ThemeOption.DARK,
                    onClick = { chosenTheme = ThemeOption.DARK }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onThemeChange(chosenTheme)
                    onDismissDialog()
                }
            ) {
                Text(text = stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissDialog) {
                Text(text = stringResource(id = R.string.delete_dialog_dismiss))
            }
        },
        modifier = modifier
    )
}

@Composable
private fun SettingsDialogChooserRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
        )
        Text(text)
    }
}

@DayNightPreview
@Composable
private fun SettingsScreenPreview() {
    JetXTheme {
        SettingsScreen(
            userSettings = UserSettings(ThemeOption.SYSTEM_DEFAULT),
            userProfile = sampleUsers[0],
            errorMessage = null,
            onThemeChange = {},
            onProfileClick = {},
            onSignOutClick = {},
            onBackClick = {},
        )
    }
}