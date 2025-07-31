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
    navController: NavController
) {
    navigation<AuthGraph>(
        startDestination = AuthDestination.Entry
    ) {
        composable<AuthDestination.Entry> {
            EntryScreen(
                onCreateAccountClick = {
                    navController.navigate(AuthDestination.SignUp)
                },
                onSignInClick = {
                    navController.navigate(AuthDestination.SignIn)
                }
            )
        }

        composable<AuthDestination.SignIn> {
            SignInRoute(
                onBackClick = navController::navigateUp
            )
        }

        composable<AuthDestination.SignUp> {
            SignUpRoute(
                onBackClick = navController::navigateUp
            )
        }
    }
}