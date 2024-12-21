package com.anshtya.jetx.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anshtya.jetx.calls.ui.navigation.calls
import com.anshtya.jetx.chats.ui.navigation.Chats
import com.anshtya.jetx.chats.ui.navigation.chats
import com.anshtya.jetx.home.navigation.TopLevelHomeDestination
import com.anshtya.jetx.home.navigation.camera
import com.anshtya.jetx.home.navigation.isDestinationInHierarchy
import com.anshtya.jetx.home.navigation.navigateToTopLevelHomeDestination
import com.anshtya.jetx.settings.ui.navigation.navigateToSettings
import com.anshtya.jetx.settings.ui.navigation.settings

@Composable
fun Home(
    navController: NavHostController = rememberNavController()
) {
    val topLevelDestinations = remember { TopLevelHomeDestination.entries }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    Log.d("foo", "${currentDestination?.route}")

    val showBottomBar = remember(currentDestination) {
        topLevelDestinations.any {
            currentDestination?.hasRoute(it.route::class) == true
        }
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(showBottomBar) {
                BottomNavigationBar(
                    destinations = topLevelDestinations,
                    currentDestination = currentDestination,
                    onNavigateToDestination = navController::navigateToTopLevelHomeDestination
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Chats
            ) {
                chats(
                    onNavigateToSettings = navController::navigateToSettings
                )
                calls()
                settings()
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    destinations: List<TopLevelHomeDestination>,
    currentDestination: NavDestination?,
    onNavigateToDestination: (Any) -> Unit
) {
    NavigationBar {
        destinations.forEach { destination ->
            val selected = currentDestination.isDestinationInHierarchy(destination.route::class)
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (destination.route == camera) {
                        // launch camera activity
                    } else {
                        onNavigateToDestination(destination.route)
                    }
                },
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

