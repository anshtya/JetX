package com.anshtya.jetx.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.ui.graphics.vector.ImageVector
import com.anshtya.jetx.R
import kotlinx.serialization.Serializable

sealed interface Graph {
    @Serializable
    data object MainGraph

    @Serializable
    data object AuthGraph
}

sealed interface Route {

    sealed interface AuthGraph : Route {
        @Serializable
        data object Onboarding : Route

        @Serializable
        data object SignIn : Route

        @Serializable
        data object SignUp : Route
    }

    sealed interface MainGraph : Route {
        @Serializable
        data object Chats : MainGraph

        @Serializable
        data object Camera : MainGraph

        @Serializable
        data object Groups : MainGraph
    }
}

enum class TopLevelDestination(
    val text: Int,
    val route: Route.MainGraph,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    CHATS(
        text = R.string.chats,
        route = Route.MainGraph.Chats,
        selectedIcon = Icons.AutoMirrored.Filled.Message,
        unselectedIcon = Icons.AutoMirrored.Outlined.Message
    ),
    CAMERA(
        text = R.string.Camera,
        route = Route.MainGraph.Camera,
        selectedIcon = Icons.Filled.PhotoCamera,
        unselectedIcon = Icons.Outlined.PhotoCamera
    ),
    GROUPS(
        text = R.string.Groups,
        route = Route.MainGraph.Groups,
        selectedIcon = Icons.Filled.Groups,
        unselectedIcon = Icons.Outlined.Groups
    )
}