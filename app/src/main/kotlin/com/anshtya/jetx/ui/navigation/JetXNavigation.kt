package com.anshtya.jetx.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.anshtya.jetx.auth.ui.navigation.authGraph
import com.anshtya.jetx.auth.ui.navigation.navigateToAuth
import com.anshtya.jetx.auth.ui.navigation.navigateToCreateProfile
import com.anshtya.jetx.home.navigation.Home
import com.anshtya.jetx.home.navigation.home
import com.anshtya.jetx.home.navigation.navigateToHome

@Composable
fun JetXNavigation(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        authGraph(
            navController = navController,
            onNavigateToHome = navController::navigateToHome
        )

        home(
            onNavigateToAuth = navController::navigateToAuth,
            onNavigateToCreateProfile = {
                navController.navigateToCreateProfile {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            }
        )
    }
}