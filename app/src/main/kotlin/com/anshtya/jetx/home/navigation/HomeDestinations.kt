package com.anshtya.jetx.home.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.ui.graphics.vector.ImageVector
import com.anshtya.jetx.R
import com.anshtya.jetx.calls.ui.navigation.CallsDestinations
import com.anshtya.jetx.chats.ui.navigation.ChatsDestinations

const val camera = "camera"

enum class TopLevelHomeDestination(
    val text: Int,
    val route: Any,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    CHATS(
        text = R.string.chats,
        route = ChatsDestinations.ChatList,
        selectedIcon = Icons.AutoMirrored.Filled.Message,
        unselectedIcon = Icons.AutoMirrored.Outlined.Message
    ),
    CAMERA(
        text = R.string.Camera,
        route = camera,
        selectedIcon = Icons.Filled.PhotoCamera,
        unselectedIcon = Icons.Filled.PhotoCamera
    ),
    CALLS(
        text = R.string.Calls,
        route = CallsDestinations.CallLogs,
        selectedIcon = Icons.Filled.Phone,
        unselectedIcon = Icons.Outlined.Phone
    )
}