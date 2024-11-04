package com.anshtya.jetx.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anshtya.jetx.ui.navigation.Route
import com.anshtya.jetx.ui.navigation.TopLevelDestination

@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController()
): JetXAppState {
    return remember(navController) {
        JetXAppState(navController)
    }
}

class JetXAppState(
    val navController: NavHostController
) {
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    val currentDestination
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    fun navigateToTopLevelDestination(route: Route.MainGraph) {
        navController.navigate(route) {
            popUpTo(Route.MainGraph.Chats) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}