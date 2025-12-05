package com.anshtya.jetx.ui.main

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.IntentCompat
import androidx.core.util.Consumer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anshtya.jetx.attachments.ui.preview.MediaPreviewActivity
import com.anshtya.jetx.calls.ui.navigation.calls
import com.anshtya.jetx.chats.ui.chat.ChatUserArgs
import com.anshtya.jetx.chats.ui.chat.toChatDestination
import com.anshtya.jetx.chats.ui.navigation.ChatsGraphRoute
import com.anshtya.jetx.chats.ui.navigation.chatsGraph
import com.anshtya.jetx.core.ui.components.scaffold.JetxScaffold
import com.anshtya.jetx.util.Constants
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
data object MainScreenWithNavBar

fun NavGraphBuilder.mainScreenWithNavBar(
    onNavigateToSettings: () -> Unit,
) {
    composable<MainScreenWithNavBar> {
        MainScreen(
            onNavigateToSettings = onNavigateToSettings
        )
    }
}

@Composable
private fun MainScreen(
    onNavigateToSettings: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val topLevelDestinations = remember { TopLevelDestination.entries }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    val context = LocalContext.current
    val activity = context as ComponentActivity

    DisposableEffect(navController) {
        val listener = Consumer<Intent> { intent -> handleIntent(intent, navController, context) }
        activity.addOnNewIntentListener(listener)
        onDispose { activity.removeOnNewIntentListener(listener) }
    }

    val showBottomBar by remember {
        derivedStateOf {
            topLevelDestinations.any {
               currentBackStackEntry?.destination?.hasRoute(it.route::class) == true
            }
        }
    }

    JetxScaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    destinations = topLevelDestinations,
                    currentDestination = currentBackStackEntry?.destination,
                    onNavigateToDestination = navController::navigateToTopLevelDestination
                )
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = ChatsGraphRoute
        ) {
            chatsGraph(
                navController = navController,
                onNavigateToSettings = onNavigateToSettings
            )
            calls()
        }

        // Handle onCreate intent
        handleIntent(activity.intent, navController, context)
    }
}

@Composable
private fun BottomNavigationBar(
    destinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    onNavigateToDestination: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier) {
        destinations.forEach { destination ->
            val selected = currentDestination.isDestinationInHierarchy(destination.route::class)
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) {
                            destination.selectedIcon
                        } else {
                            destination.unselectedIcon
                        },
                        contentDescription = stringResource(id = destination.text)
                    )
                },
                label = {
                    Text(text = stringResource(id = destination.text))
                }
            )
        }
    }
}

private fun NavDestination?.isDestinationInHierarchy(
    route: KClass<*>
): Boolean {
    return this?.hierarchy?.any { it.hasRoute(route) } == true
}

private fun <T : Any> NavController.navigateToTopLevelDestination(route: T) {
    navigate(route) {
        popUpTo(ChatsGraphRoute) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

private fun handleIntent(
    intent: Intent,
    navController: NavController,
    context: Context
) {
    // Handle open conversation
    if (intent.data?.host == Constants.APP_HOST) {
        val chatId = intent.data?.getQueryParameter(Constants.CHAT_ID_ARG)
        if (chatId != null) {
            navController.navigate(ChatUserArgs(chatId = chatId.toInt()).toChatDestination())
            return
        }
    }

    // Handle media preview
    val chatIds = intent.getIntegerArrayListExtra(Constants.CHAT_IDS_INTENT_KEY)
    val uris = IntentCompat.getParcelableArrayListExtra(
        intent,
        Intent.EXTRA_STREAM,
        Parcelable::class.java
    )
    if (chatIds != null && uris != null) {
        if (chatIds.size == 1) {
            navController.navigate(ChatUserArgs(chatId = chatIds.first()).toChatDestination())
        }
        context.startActivity(
            Intent(context, MediaPreviewActivity::class.java).apply {
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                putIntegerArrayListExtra(Constants.CHAT_IDS_INTENT_KEY, chatIds)
            }
        )
        return
    }
}