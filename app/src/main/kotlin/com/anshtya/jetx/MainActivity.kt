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
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.anshtya.jetx.ui.navigation.JetXNavigation
import com.anshtya.jetx.ui.theme.JetXTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private var useDarkTheme: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    useDarkTheme = it.useDarkTheme
                }
            }
        }

        splashScreen.setKeepOnScreenCondition {
            useDarkTheme == null
        }

        setContent {
            val useDarkTheme = shouldUseDarkTheme(useDarkTheme)
            JetXTheme(
                darkTheme = useDarkTheme
            ) {
                Surface(Modifier.fillMaxSize()) {
                    JetXNavigation()
                }
            }
        }
    }
}

@Composable
private fun shouldUseDarkTheme(
    useDarkTheme: Boolean?
): Boolean {
    return if (useDarkTheme == null) {
        isSystemInDarkTheme()
    } else if (useDarkTheme) {
        true
    } else {
        false
    }
}