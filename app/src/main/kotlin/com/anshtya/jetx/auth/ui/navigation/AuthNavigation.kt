package com.anshtya.jetx.auth.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.anshtya.jetx.auth.ui.EntryScreen
import com.anshtya.jetx.auth.ui.signin.SignInRoute
import com.anshtya.jetx.auth.ui.signup.SignUpRoute
import kotlinx.serialization.Serializable

@Serializable
data object AuthGraph

fun NavGraphBuilder.authGraph(
    navController: NavController,
    onNavigateToHome: () -> Unit,
    onNavigateToCreateProfile: () -> Unit
) {
    navigation<AuthGraph>(
        startDestination = AuthDestinations.Entry
    ) {
        composable<AuthDestinations.Entry> {
            EntryScreen(
                onCreateAccountClick = {
                    navController.navigate(AuthDestinations.SignUp)
                },
                onSignInClick = {
                    navController.navigate(AuthDestinations.SignIn)
                }
            )
        }

        composable<AuthDestinations.SignIn> {
            SignInRoute(
                onNavigateToHome = onNavigateToHome,
                onNavigateToCreateProfile = onNavigateToCreateProfile,
                onBackClick = navController::navigateUp
            )
        }

        composable<AuthDestinations.SignUp> {
            SignUpRoute(
                onContinueClick = onNavigateToCreateProfile,
                onBackClick = navController::navigateUp
            )
        }
    }
}