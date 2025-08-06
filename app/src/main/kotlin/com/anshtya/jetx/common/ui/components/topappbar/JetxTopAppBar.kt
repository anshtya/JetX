package com.anshtya.jetx.common.ui.components.topappbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JetxTopAppBar(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit) = {},
    navigationIcon: @Composable (() -> Unit) = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = title,
        navigationIcon = navigationIcon,
        actions = actions,
        modifier = modifier
    )
}