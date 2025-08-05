package com.anshtya.jetx.common.ui.components.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun JetxScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable (() -> Unit) = {},
    bottomBar: @Composable (() -> Unit) = {},
    snackbarHost: @Composable (() -> Unit) = {},
    contentWindowInsets: WindowInsets = ScaffoldDefaults
        .contentWindowInsets
        .union(WindowInsets.displayCutout),
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        contentWindowInsets = contentWindowInsets,
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .imePadding()
        ) {
            content()
        }
    }
}