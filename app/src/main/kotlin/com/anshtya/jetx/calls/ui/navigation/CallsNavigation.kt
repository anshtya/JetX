package com.anshtya.jetx.calls.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anshtya.jetx.calls.ui.CallLogsScreen

fun NavGraphBuilder.callLogsScreen() {
    composable<CallsDestinations.CallLogs> {
        CallLogsScreen()
    }
}