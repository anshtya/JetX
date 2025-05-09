package com.anshtya.jetx.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anshtya.jetx.attachments.imageScreen
import com.anshtya.jetx.attachments.navigateToImageScreen
import com.anshtya.jetx.calls.ui.navigation.calls
import com.anshtya.jetx.chats.ui.navigation.Chats
import com.anshtya.jetx.chats.ui.navigation.chats
import com.anshtya.jetx.home.navigation.TopLevelHomeDestination
import com.anshtya.jetx.home.navigation.isDestinationInHierarchy
import com.anshtya.jetx.home.navigation.navigateToTopLevelHomeDestination
import com.anshtya.jetx.settings.ui.navigation.navigateToSettings
import com.anshtya.jetx.settings.ui.navigation.settings

@Composable
fun Home(
    onNavigateToAuth: () -> Unit,
    onNavigateToCreateProfile: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val userState by viewModel.userState.collectAsStateWithLifecycle()

    userState?.let {
        if (it.authenticated && it.profileCreated) {
            Home()
        } else {
            LaunchedEffect(Unit) {
                if (!it.authenticated) {
                    onNavigateToAuth()
                } else {
                    onNavigateToCreateProfile()
                }
            }
        }
    }
}

@Composable
private fun Home() {
    val navController = rememberNavController()
    val topLevelDestinations = remember { TopLevelHomeDestination.entries }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    val showBottomBar = remember(currentDestination) {
        topLevelDestinations.any {
            currentDestination?.hasRoute(it.route::class) == true
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
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
                .imePadding()
        ) {
            NavHost(
                navController = navController,
                startDestination = Chats
            ) {
                chats(
                    navController = navController,
                    onNavigateToSettings = navController::navigateToSettings,
                    onNavigateToImageScreen = navController::navigateToImageScreen
                )
                calls()
                settings(onBackClick = navController::navigateUp)

                imageScreen(onBackClick = navController::popBackStack)
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

