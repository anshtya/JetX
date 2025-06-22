package com.anshtya.jetx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.anshtya.jetx.common.model.ThemeOption
import com.anshtya.jetx.ui.theme.JetXTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private var uiState by mutableStateOf<MainActivityUiState>(MainActivityUiState.Loading)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState = it }
            }
        }

        splashScreen.setKeepOnScreenCondition { uiState is MainActivityUiState.Loading }

        setContent {
            val useDarkTheme = shouldUseDarkTheme(uiState)
            JetXTheme(
                darkTheme = useDarkTheme
            ) {
                App(Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun shouldUseDarkTheme(
    uiState: MainActivityUiState
): Boolean {
    return when (uiState) {
        is MainActivityUiState.Loading -> isSystemInDarkTheme()
        is MainActivityUiState.Success -> when (uiState.uiProperties.theme) {
            ThemeOption.SYSTEM_DEFAULT -> isSystemInDarkTheme()
            ThemeOption.LIGHT -> false
            ThemeOption.DARK -> true
        }
    }
}