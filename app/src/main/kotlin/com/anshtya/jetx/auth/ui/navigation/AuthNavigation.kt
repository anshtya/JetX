package com.anshtya.jetx.auth.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.anshtya.jetx.auth.ui.createprofile.CreateProfileRoute
import com.anshtya.jetx.auth.ui.entry.EntryScreen
import com.anshtya.jetx.auth.ui.signin.SignInRoute
import com.anshtya.jetx.auth.ui.signup.SignUpRoute
import kotlinx.serialization.Serializable

@Serializable
private data object AuthGraph

fun NavGraphBuilder.authGraph(
    navController: NavController,
    onNavigateToHome: (NavOptionsBuilder.() -> Unit) -> Unit
) {
    navigation<AuthGraph>(
        startDestination = AuthDestinations.Entry
    ) {
        composable<AuthDestinations.Entry> {
            EntryScreen(
                onCreateAccountClick = navController::navigateToSignUp,
                onSignInClick = navController::navigateToSignIn
            )
        }

        composable<AuthDestinations.SignIn> {
            SignInRoute(
                onNavigateToHome = {
                    onNavigateToHome(navigateToHomeNavOptions())
                },
                onBackClick = navController::navigateUp
            )
        }

        composable<AuthDestinations.SignUp> {
            SignUpRoute(
                onContinueClick = {
                    navController.navigateToCreateProfile {
                        popUpTo(AuthDestinations.SignUp) {
                            inclusive = true
                        }
                    }
                },
                onBackClick = navController::navigateUp
            )
        }

        composable<AuthDestinations.CreateProfile> {
            CreateProfileRoute(
                onNavigateToHome = {
                    onNavigateToHome(navigateToHomeNavOptions())
                },
                onNavigateUp = navController::navigateUp
            )
        }
    }
}

fun NavController.navigateToAuth(
    navOptionsBuilder: NavOptionsBuilder.() -> Unit
) {
    navigate(
        route = AuthGraph,
        builder = navOptionsBuilder
    )
}

fun NavController.navigateToCreateProfile(
    navOptionsBuilder: NavOptionsBuilder.() -> Unit
) {
    navigate(
        route = AuthDestinations.CreateProfile,
        builder = navOptionsBuilder
    )
}

private fun NavController.navigateToSignIn() {
    navigate(AuthDestinations.SignIn)
}

private fun NavController.navigateToSignUp() {
    navigate(AuthDestinations.SignUp)
}

private fun navigateToHomeNavOptions(): NavOptionsBuilder.() -> Unit {
    return {
        popUpTo<AuthGraph> {
            inclusive = true
        }
    }
}