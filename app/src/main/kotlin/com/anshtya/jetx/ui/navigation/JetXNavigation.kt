package com.anshtya.jetx.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.anshtya.jetx.auth.ui.navigation.AuthGraph
import com.anshtya.jetx.auth.ui.navigation.authGraph
import com.anshtya.jetx.chats.ui.navigation.Chats
import com.anshtya.jetx.profile.ui.CreateProfileRoute
import com.anshtya.jetx.ui.navigation.authcheck.AuthCheck
import com.anshtya.jetx.ui.navigation.home.HomeGraph
import com.anshtya.jetx.ui.navigation.home.homeGraph

@Composable
fun JetXNavigation(
    navController: NavHostController,
    onSetGraph: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = AuthCheck
    ) {
        composable<AuthCheck> {
            AuthCheck(
                onNavigateToChatList = {
                    navController.navigate(route = Chats, authCheckNavOptions())
                },
                onNavigateToAuth = {
                    navController.navigate(route = AuthGraph, authCheckNavOptions())
                },
                onNavigateToCreateProfile = {
                    navController.navigate(route = CreateProfileRoute, authCheckNavOptions())
                }
            )
        }

        authGraph(
            navController = navController,
            onNavigateToHome = {
                navController.navigate(HomeGraph) {
                    popUpTo(AuthGraph) { inclusive = true }
                }
            },
            onNavigateToCreateProfile = {
                navController.navigate(CreateProfileRoute) {
                    popUpTo(AuthGraph) { inclusive = true }
                }
            }
        )

        composable<CreateProfileRoute> {
            CreateProfileRoute(
                onNavigateToHome = {
                    navController.navigate(HomeGraph) {
                        popUpTo(CreateProfileRoute) { inclusive = true }
                    }
                },
                onNavigateUp = navController::navigateUp
            )
        }

        homeGraph(
            navController = navController,
            onNavigateToAuth = {
                navController.navigate(AuthGraph) {
                    popUpTo<HomeGraph> { inclusive = true }
                }
            }
        )
    }
    onSetGraph()
}

private fun authCheckNavOptions(): NavOptionsBuilder.() -> Unit {
    return {
        popUpTo<AuthCheck> {
            inclusive = true
        }
    }
}