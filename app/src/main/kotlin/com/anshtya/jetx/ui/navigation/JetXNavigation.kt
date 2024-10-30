package com.anshtya.jetx.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.anshtya.jetx.ui.features.OnboardingScreen
import com.anshtya.jetx.ui.features.auth.signin.SignInRoute
import com.anshtya.jetx.ui.features.auth.signup.SignUpRoute

@Composable
fun JetXNavigation(
    navController: NavHostController,
    hasOnboarded: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (hasOnboarded) {
            Graph.MainGraph
        } else {
            Graph.AuthGraph
        }
    ) {
        navigation<Graph.AuthGraph>(
            startDestination = Route.AuthGraph.Onboarding
        ) {
            composable<Route.AuthGraph.Onboarding> {
                OnboardingScreen(
                    onCreateAccountClick = {
                        navController.navigate(Route.AuthGraph.SignUp)
                    },
                    onSignInClick = {
                        navController.navigate(Route.AuthGraph.SignIn)
                    }
                )
            }

            composable<Route.AuthGraph.SignIn> {
                SignInRoute(
                    onSignInSuccessful = navController::navigateOnAuth,
                    onBackClick = navController::navigateUp
                )
            }

            composable<Route.AuthGraph.SignUp> {
                SignUpRoute(
                    onContinueClick = navController::navigateOnAuth,
                    onBackClick = navController::navigateUp
                )
            }
        }

        navigation<Graph.MainGraph>(
            startDestination = Route.MainGraph.Chats
        ) {
            composable<Route.MainGraph.Chats> {
                Text("hi")
            }

            composable<Route.MainGraph.Camera> { }

            composable<Route.MainGraph.Groups> { }
        }
    }
}