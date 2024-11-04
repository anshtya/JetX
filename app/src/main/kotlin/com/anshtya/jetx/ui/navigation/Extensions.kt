package com.anshtya.jetx.ui.navigation

import androidx.navigation.NavController

fun NavController.logOut() {
    navigate(Graph.AuthGraph) {
        popUpTo(Graph.MainGraph) { inclusive = true }
    }
}

fun NavController.navigateOnAuth() {
    navigate(Graph.MainGraph) {
        popUpTo(Graph.AuthGraph) { inclusive = true }
    }
}