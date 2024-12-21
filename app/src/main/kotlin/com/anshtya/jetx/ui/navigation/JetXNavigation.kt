package com.anshtya.jetx.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.anshtya.jetx.chats.ui.ChatsRoute
import com.anshtya.jetx.onboarding.OnboardingScreen
import com.anshtya.jetx.auth.ui.signin.SignInRoute
import com.anshtya.jetx.auth.ui.signup.SignUpRoute
import androidx.navigation.compose.rememberNavController
import com.anshtya.jetx.home.navigation.Home
import com.anshtya.jetx.home.navigation.home

@Composable
fun JetXNavigation(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Home
    ) {


        home()
    }
}