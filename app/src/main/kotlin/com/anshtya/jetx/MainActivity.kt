package com.anshtya.jetx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshtya.jetx.core.preferences.model.ThemeOption
import com.anshtya.jetx.ui.app.App
import com.anshtya.jetx.ui.theme.JetXTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        var showSplashScreen = true
        installSplashScreen().setKeepOnScreenCondition { showSplashScreen }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            JetXTheme(
                darkTheme = shouldUseDarkTheme(state.theme)
            ) {
                Surface(Modifier.fillMaxSize()) {
                    App(
                        onHideSplashScreen = { showSplashScreen = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun shouldUseDarkTheme(
    theme: ThemeOption
): Boolean {
    return when (theme) {
        ThemeOption.SYSTEM_DEFAULT -> isSystemInDarkTheme()
        ThemeOption.LIGHT -> false
        ThemeOption.DARK -> true
    }
}