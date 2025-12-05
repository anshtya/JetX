package com.anshtya.jetx.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.anshtya.jetx.onboarding.navigation.OnboardingGraph
import com.anshtya.jetx.onboarding.navigation.onboardingGraph
import com.anshtya.jetx.profile.ui.CreateProfileRoute
import com.anshtya.jetx.registration.navigation.RegistrationGraph
import com.anshtya.jetx.registration.navigation.registrationGraph
import com.anshtya.jetx.ui.LoadingRoute
import com.anshtya.jetx.ui.main.MainGraph
import com.anshtya.jetx.ui.main.mainGraph

@Composable
fun App(
    onHideSplashScreen: () -> Unit,
    viewModel: AppViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    val navState by viewModel.navState.collectAsStateWithLifecycle()

    val initialised by remember {
        derivedStateOf { navState != AppNavState.Initialising }
    }
    LaunchedEffect(initialised) {
        if (initialised) onHideSplashScreen()
    }

    NavHost(
        navController = navController,
        startDestination = LoadingRoute
    ) {
        composable<LoadingRoute> {
            LoadingRoute()
        }

        onboardingGraph(navController = navController)

        registrationGraph(navController = navController)

        composable<CreateProfileRoute> {
            CreateProfileRoute()
        }

        mainGraph(navController = navController)
    }

    LaunchedEffect(navState) {
        val appNavOptions = navOptions {
            popUpTo(navController.graph.id)
            launchSingleTop = true
        }

        when (navState) {
            AppNavState.Initialising -> {}

            AppNavState.Onboarding -> {
                navController.navigate(OnboardingGraph, appNavOptions)
            }

            AppNavState.CreateProfile -> {
                navController.navigate(CreateProfileRoute, appNavOptions)
            }

            AppNavState.Authenticated -> {
                navController.navigate(MainGraph, appNavOptions)
            }

            AppNavState.Unauthenticated -> {
                navController.navigate(RegistrationGraph, appNavOptions)
            }
        }
    }
}