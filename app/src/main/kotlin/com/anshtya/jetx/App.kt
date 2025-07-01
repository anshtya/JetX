package com.anshtya.jetx

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anshtya.jetx.attachments.ui.preview.MediaPreviewActivity
import com.anshtya.jetx.chats.ui.chat.ChatUserArgs
import com.anshtya.jetx.chats.ui.chat.toChatDestination
import com.anshtya.jetx.chats.ui.navigation.ChatsDestinations
import com.anshtya.jetx.ui.navigation.JetXNavigation
import com.anshtya.jetx.ui.navigation.home.TopLevelHomeDestination
import com.anshtya.jetx.util.Constants
import kotlin.reflect.KClass

@Composable
fun App(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val topLevelHomeDestinations = remember { TopLevelHomeDestination.entries }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    val showBottomBar = remember(currentDestination) {
        topLevelHomeDestinations.any {
            currentDestination?.hasRoute(it.route::class) == true
        }
    }

    val context = LocalContext.current
    val activity = context as ComponentActivity

    DisposableEffect(navController) {
        val listener = Consumer<Intent> { intent -> handleIntent(intent, navController, context) }
        activity.addOnNewIntentListener(listener)
        onDispose { activity.removeOnNewIntentListener(listener) }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    destinations = topLevelHomeDestinations,
                    currentDestination = currentDestination,
                    onNavigateToDestination = navController::navigateToTopLevelHomeDestination
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .imePadding()
        ) {
            JetXNavigation(
                navController = navController,
                onSetGraph = {
                    // Handle onCreate intent
                    handleIntent(activity.intent, navController, context)
                }
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(
    destinations: List<TopLevelHomeDestination>,
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

fun NavDestination?.isDestinationInHierarchy(
    route: KClass<*>
): Boolean {
    return this?.hierarchy?.any { it.hasRoute(route) } == true
}

fun <T : Any> NavController.navigateToTopLevelHomeDestination(route: T) {
    navigate(route) {
        popUpTo(ChatsDestinations.ChatList) {
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
    }
}