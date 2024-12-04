package com.anshtya.jetx.chats.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.anshtya.jetx.R

@Composable
fun TopAppBarDropdownMenu(
    expanded: Boolean,
    onIconClick: () -> Unit,
    onDismissRequest: () -> Unit,
    onMenuItemClick: (MenuOption) -> Unit
) {
    IconButton(
        onClick = onIconClick
    ) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = stringResource(R.string.menu)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.starred_messages))
                },
                onClick = { onMenuItemClick(MenuOption.STARRED_MESSAGES) }
            )
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.settings))
                },
                onClick = { onMenuItemClick(MenuOption.SETTINGS) }
            )
        }
    }
}

enum class MenuOption {
    STARRED_MESSAGES,
    SETTINGS
}