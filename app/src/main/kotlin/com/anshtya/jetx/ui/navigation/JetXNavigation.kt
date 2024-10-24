package com.anshtya.jetx.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
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
        composable<Route.Onboarding> {

        }

        navigation<Graph.AuthGraph>(
            startDestination = Route.AuthGraph.SignIn
        ) {
            composable<Route.AuthGraph.SignIn> {
                SignInRoute(onSignUpClick = {navController.navigate(Route.AuthGraph.SignUp)})
            }

            composable<Route.AuthGraph.SignUp> {
                SignUpRoute()
            }
        }

        navigation<Graph.MainGraph>(
            startDestination = Route.MainGraph.Chats
        ) {
            composable<Route.MainGraph.Chats> {
                Text("hi")
            }

            composable<Route.MainGraph.Camera> {  }

            composable<Route.MainGraph.Groups> {  }
        }
    }
}