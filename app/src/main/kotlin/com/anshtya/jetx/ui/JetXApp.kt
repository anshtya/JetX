package com.anshtya.jetx.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.anshtya.jetx.ui.navigation.JetXNavigation
import com.anshtya.jetx.ui.navigation.Route
import com.anshtya.jetx.ui.navigation.TopLevelDestination
import kotlin.reflect.KClass

@Composable
fun JetXApp(
    hasOnboarded: Boolean,
    appState: JetXAppState = rememberAppState()
) {
    val currentDestination = appState.currentDestination

    val showBottomBar = remember(currentDestination) {
        appState.topLevelDestinations.any {
            currentDestination?.hasRoute(it.route::class) == true
        }
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(showBottomBar) {
                BottomNavigationBar(
                    destinations = appState.topLevelDestinations,
                    currentDestination = currentDestination,
                    onNavigateToDestination = appState::navigateToTopLevelDestination
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            JetXNavigation(
                navHostController = appState.navController,
                hasOnboarded = hasOnboarded,
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(
    destinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    onNavigateToDestination: (Route.MainGraph) -> Unit
) {
    NavigationBar {
        destinations.forEach { destination ->
            val selected = currentDestination.isDestinationInHierarchy(destination.route::class)
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) {
                            destination.selectedIcon
                        } else {
                            destination.unselectedIcon
                        },
                        contentDescription = stringResource(id = destination.text)
                    )
                },
                label = {
                    Text(text = stringResource(id = destination.text))
                }
            )
        }
    }
}

private fun NavDestination?.isDestinationInHierarchy(
    route: KClass<*>
): Boolean {
    return this?.hierarchy?.any { it.hasRoute(route) } == true
}