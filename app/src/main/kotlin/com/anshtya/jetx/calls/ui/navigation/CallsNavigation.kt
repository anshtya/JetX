package com.anshtya.jetx.calls.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.anshtya.jetx.calls.ui.CallLogsScreen
import kotlinx.serialization.Serializable

@Serializable
private data object Calls

fun NavGraphBuilder.calls() {
    navigation<Calls>(
        startDestination = CallsDestinations.CallLogs
    ) {
        composable<CallsDestinations.CallLogs> {
            CallLogsScreen()
        }
    }
}