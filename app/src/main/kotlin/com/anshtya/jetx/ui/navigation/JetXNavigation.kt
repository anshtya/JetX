package com.anshtya.jetx.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation

@Composable
fun JetXNavigation(
    navHostController: NavHostController,
    hasOnboarded: Boolean
) {
    NavHost(
        navController = navHostController,
        startDestination = if (hasOnboarded) {
            Graph.MainGraph
        } else {
            Route.Onboarding
        }
    ) {
        composable<Route.Onboarding> {

        }

//        navigation<Graph.AuthGraph>(
//            startDestination = Any()
//        ) {}

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