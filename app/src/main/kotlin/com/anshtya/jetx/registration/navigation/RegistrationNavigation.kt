package com.anshtya.jetx.registration.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
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
            _root_ide_package_.com.anshtya.jetx.registration.EnterPhoneNumberRoute(
                onNavigateUp = navController::navigateUp,
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
            _root_ide_package_.com.anshtya.jetx.registration.SetupPinRoute(
                onNavigateUp = navController::navigateUp,
                viewModel = hiltViewModel(navBackStackEntry)
            )
        }
    }
}