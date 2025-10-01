package com.anshtya.jetx.registration.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.anshtya.jetx.registration.EnterPhoneNumberRoute
import com.anshtya.jetx.registration.SetupPinRoute
import kotlinx.serialization.Serializable

@Serializable
data object RegistrationGraph

fun NavGraphBuilder.registrationGraph(
    navController: NavController
) {
    navigation<RegistrationGraph>(
        startDestination = RegistrationDestination.EnterPhoneNumber
    ) {
        composable<RegistrationDestination.EnterPhoneNumber> { backStackEntry ->
            val navBackStackEntry = remember(backStackEntry) {
                navController.getBackStackEntry<RegistrationGraph>()
            }
            EnterPhoneNumberRoute(
                onNavigateToSetupPin = {
                    navController.navigate(RegistrationDestination.SetupPin)
                },
                viewModel = hiltViewModel(navBackStackEntry)
            )
        }
        composable<RegistrationDestination.SetupPin> { backStackEntry ->
            val navBackStackEntry = remember(backStackEntry) {
                navController.getBackStackEntry<RegistrationGraph>()
            }
            SetupPinRoute(
                onNavigateUp = navController::navigateUp,
                viewModel = hiltViewModel(navBackStackEntry)
            )
        }
    }
}