package com.anshtya.jetx.ui.app

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.IntentCompat
import androidx.core.util.Consumer
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.anshtya.jetx.attachments.ui.preview.MediaPreviewActivity
import com.anshtya.jetx.auth.ui.navigation.AuthGraph
import com.anshtya.jetx.auth.ui.navigation.authGraph
import com.anshtya.jetx.chats.ui.chat.ChatUserArgs
import com.anshtya.jetx.chats.ui.chat.toChatDestination
import com.anshtya.jetx.chats.ui.navigation.ChatsDestination
import com.anshtya.jetx.profile.ui.CreateProfileRoute
import com.anshtya.jetx.ui.LoadingRoute
import com.anshtya.jetx.ui.authenticated.AuthenticatedDestination
import com.anshtya.jetx.ui.authenticated.AuthenticatedGraph
import com.anshtya.jetx.ui.authenticated.authenticatedGraph
import com.anshtya.jetx.util.Constants
import kotlin.reflect.KClass

@Composable
fun App(
    onHideSplashScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AppViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    val authenticatedDestinations = remember { AuthenticatedDestination.entries }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    val showBottomBar = remember(currentDestination) {
        authenticatedDestinations.any {
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

    val navState by viewModel.navState.collectAsStateWithLifecycle()

    val initialised by remember {
        derivedStateOf { navState != AppNavState.Initialising }
    }
    LaunchedEffect(initialised) {
        if (initialised) onHideSplashScreen()
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    destinations = authenticatedDestinations,
                    currentDestination = currentDestination,
                    onNavigateToDestination = navController::navigateToAuthenticatedDestination
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
            NavHost(
                navController = navController,
                startDestination = LoadingRoute
            ) {
                composable<LoadingRoute> {
                    LoadingRoute()
                }

                authGraph(navController = navController)

                composable<CreateProfileRoute> {
                    CreateProfileRoute(
                        onNavigateUp = navController::navigateUp
                    )
                }

                authenticatedGraph(navController = navController)
            }

            // Handle onCreate intent
            handleIntent(activity.intent, navController, context)

            LaunchedEffect(navState) {
                val appNavOptions = navOptions {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }

                when (navState) {
                    AppNavState.Authenticated -> {
                        navController.navigate(AuthenticatedGraph, appNavOptions)
                    }

                    AppNavState.CreateProfile -> {
                        navController.navigate(CreateProfileRoute, appNavOptions)
                    }

                    AppNavState.Unauthenticated -> {
                        navController.navigate(AuthGraph, appNavOptions)
                    }

                    AppNavState.Initialising -> {}
                }
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    destinations: List<AuthenticatedDestination>,
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

fun <T : Any> NavController.navigateToAuthenticatedDestination(route: T) {
    navigate(route) {
        popUpTo(ChatsDestination.ChatList) {
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