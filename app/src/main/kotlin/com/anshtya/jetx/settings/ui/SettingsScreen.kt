package com.anshtya.jetx.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshtya.jetx.R
import com.anshtya.jetx.common.ui.BackButton
import com.anshtya.jetx.common.ui.ComponentPreview
import com.anshtya.jetx.settings.data.model.ThemeOption

@Composable
fun SettingsRoute(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()

    SettingsScreen(
        userSettings = userSettings,
        onSignOutClick = viewModel::onSignOutClick,
        onThemeChange = viewModel::changeTheme,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    userSettings: UserSettings?,
    onThemeChange: (ThemeOption) -> Unit,
    onSignOutClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var showThemeDialog by rememberSaveable { mutableStateOf(false) }
    if (showThemeDialog) {
        ThemeSelectDialog(
            currentTheme = userSettings!!.theme,
            onDismissDialog = { showThemeDialog = false },
            onThemeChange = onThemeChange
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.settings))
                },
                navigationIcon = {
                    BackButton { onBackClick() }
                }
            )
        }
    ) { innerPadding ->
        if (userSettings != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
            ) {
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
private fun SettingsItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryText: String = "",
    icon: ImageVector? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(40.dp)
        ) {
            icon?.let {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            if (secondaryText.isNotBlank()) {
                Text(
                    text = secondaryText,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
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
                Text(text = stringResource(id = R.string.delete_chat_dialog_dismiss))
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

@Preview
@Composable
private fun SettingsItemPreview() {
    ComponentPreview {
        SettingsItem(
            text = "Main",
            onClick = {}
        )
    }
}