package com.anshtya.jetx.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.anshtya.jetx.chats.ui.navigation.ChatsDestinations
import com.anshtya.jetx.home.Home
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
data object Home

fun NavGraphBuilder.home(
    onNavigateToAuth: () -> Unit,
    onNavigateToCreateProfile: () -> Unit
) {
    composable<Home> {
        Home(
            onNavigateToAuth = onNavigateToAuth,
            onNavigateToCreateProfile = onNavigateToCreateProfile,
        )
    }
}

fun NavDestination?.isDestinationInHierarchy(
    route: KClass<*>
): Boolean {
    return this?.hierarchy?.any { it.hasRoute(route) } == true
}

fun <T: Any> NavController.navigateToTopLevelHomeDestination(route: T) {
    navigate(route) {
        popUpTo(ChatsDestinations.ChatList) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavController.navigateToHome(
    navOptionsBuilder: NavOptionsBuilder.() -> Unit
) {
    navigate(
        route = Home,
        builder = navOptionsBuilder
    )
}