package com.anshtya.jetx.core.ui.components.button

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.anshtya.jetx.R

@Composable
fun IconButtonDropdownMenu(
    expanded: Boolean,
    onIconClick: () -> Unit,
    onDismissRequest: () -> Unit,
    menuContent: @Composable ColumnScope.(() -> Unit) -> Unit
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
            onDismissRequest = onDismissRequest,
            content = {
                menuContent(onDismissRequest)
            }
        )
    }
}