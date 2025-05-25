package com.anshtya.jetx.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.anshtya.jetx.auth.ui.navigation.authGraph
import com.anshtya.jetx.auth.ui.navigation.navigateToAuth
import com.anshtya.jetx.auth.ui.navigation.navigateToCreateProfile
import com.anshtya.jetx.chats.ui.navigation.navigateToChats
import com.anshtya.jetx.ui.navigation.authcheck.AuthCheck
import com.anshtya.jetx.ui.navigation.home.HomeGraph
import com.anshtya.jetx.ui.navigation.home.homeGraph
import com.anshtya.jetx.ui.navigation.home.navigateToHome

@Composable
fun JetXNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = AuthCheck
    ) {
        composable<AuthCheck> {
            AuthCheck(
                onNavigateToChatList = {
                    navController.navigateToChats(navigateFromAuthCheckNavOptions())
                },
                onNavigateToAuth = {
                    navController.navigateToAuth(navigateFromAuthCheckNavOptions())
                },
                onNavigateToCreateProfile = {
                    navController.navigateToCreateProfile(navigateFromAuthCheckNavOptions())
                }
            )
        }

        authGraph(
            navController = navController,
            onNavigateToHome = navController::navigateToHome
        )

        homeGraph(
            navController = navController,
            onNavigateToAuth = {
                navController.navigateToAuth {
                    popUpTo<HomeGraph> { inclusive = true }
                }
            }
        )
    }
}

private fun navigateFromAuthCheckNavOptions(): NavOptionsBuilder.() -> Unit {
    return {
        popUpTo<AuthCheck> {
            inclusive = true
        }
    }
}