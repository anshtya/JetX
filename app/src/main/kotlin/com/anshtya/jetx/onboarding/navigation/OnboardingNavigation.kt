package com.anshtya.jetx.onboarding.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.anshtya.jetx.onboarding.EntryScreen
import com.anshtya.jetx.onboarding.RequestPermissionRoute
import kotlinx.serialization.Serializable

@Serializable
data object OnboardingGraph

fun NavGraphBuilder.onboardingGraph(
    navController: NavController
) {
    navigation<OnboardingGraph>(
        startDestination = OnboardingDestination.Entry
    ) {
        composable<OnboardingDestination.Entry> {
            EntryScreen(
                onGetStartedClick = {
                    navController.navigate(OnboardingDestination.RequestPermission)
                }
            )
        }
        composable<OnboardingDestination.RequestPermission> {
            RequestPermissionRoute(
                onNavigateUp = navController::navigateUp
            )
        }
    }
}