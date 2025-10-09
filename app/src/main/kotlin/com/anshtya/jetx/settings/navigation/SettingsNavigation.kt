package com.anshtya.jetx.settings.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.anshtya.jetx.settings.SettingsRoute
import com.anshtya.jetx.settings.profile.ViewProfileRoute
import com.anshtya.jetx.settings.profile.editname.EditNameRoute
import com.anshtya.jetx.settings.profile.editusername.EditUsernameRoute
import kotlinx.serialization.Serializable

@Serializable
private data object SettingsGraph

fun NavGraphBuilder.settingsGraph(
    navController: NavController
) {
    navigation<SettingsGraph>(
        startDestination = SettingsDestination.SettingList
    ) {
        composable<SettingsDestination.SettingList> { backStackEntry ->
            val navBackStackEntry = remember(backStackEntry) {
                navController.getBackStackEntry<SettingsGraph>()
            }
            SettingsRoute(
                onBackClick = navController::navigateUp,
                onProfileClick = {
                    navController.navigate(SettingsDestination.UserProfile.View)
                },
                viewModel = hiltViewModel(navBackStackEntry)
            )
        }

        navigation<SettingsDestination.UserProfileGraph>(
            startDestination = SettingsDestination.UserProfile.View
        ) {
            composable<SettingsDestination.UserProfile.View> { backStackEntry ->
                val navBackStackEntry = remember(backStackEntry) {
                    navController.getBackStackEntry<SettingsGraph>()
                }
                ViewProfileRoute(
                    onNavigateUp = navController::navigateUp,
                    onNavigateToEditName = {
                        navController.navigate(SettingsDestination.UserProfile.EditName(it))
                    },
                    onNavigateToEditUsername = {
                        navController.navigate(SettingsDestination.UserProfile.EditUsername(it))
                    },
                    settingsViewModel = hiltViewModel(navBackStackEntry)
                )
            }

            composable<SettingsDestination.UserProfile.EditName> {
                EditNameRoute(
                    onNavigateUp = navController::navigateUp
                )
            }

            composable<SettingsDestination.UserProfile.EditUsername> {
                EditUsernameRoute(
                    onNavigateUp = navController::navigateUp
                )
            }
        }
    }
}

fun NavController.navigateToSettingsGraph() {
    navigate(SettingsGraph)
}