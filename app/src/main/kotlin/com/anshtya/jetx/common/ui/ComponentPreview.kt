package com.anshtya.jetx.common.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.anshtya.jetx.ui.theme.JetXTheme

@Composable
fun ComponentPreview(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    JetXTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        content =  {
            Surface { content() }
        }
    )
}