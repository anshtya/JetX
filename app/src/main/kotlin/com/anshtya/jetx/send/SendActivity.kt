package com.anshtya.jetx.send

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.IntentCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.anshtya.jetx.MainActivity
import com.anshtya.jetx.auth.data.AuthRepository
import com.anshtya.jetx.auth.data.model.AuthStatus
import com.anshtya.jetx.ui.theme.JetXTheme
import com.anshtya.jetx.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SendActivity : ComponentActivity() {
    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var isAuthenticated by mutableStateOf<Boolean?>(null)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authRepository.authStatus.collect { authStatus ->
                    if (authStatus is AuthStatus.Success) {
                        isAuthenticated = authStatus.authenticated
                        if (isAuthenticated == false) {
                            val intent = Intent(this@SendActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }

        setContent {
            JetXTheme {
                if (isAuthenticated == true) {
                    SendScreen(
                        onNavigateUp = this::finish,
                        onSend = { chatIds ->
                            createSendIntent(intent, chatIds)
                        }
                    )
                }
            }
        }
    }

    private fun createSendIntent(
        intent: Intent,
        chatIds: Set<Int>
    ) {
        val newIntent = Intent()
        when (intent.action) {
            Intent.ACTION_SEND -> {
                val uri = IntentCompat.getParcelableExtra(
                    intent,
                    Intent.EXTRA_STREAM,
                    Parcelable::class.java
                )
                uri?.let {
                    newIntent.putParcelableArrayListExtra(
                        Intent.EXTRA_STREAM,
                        ArrayList(listOf(uri))
                    )
                }
            }

            Intent.ACTION_SEND_MULTIPLE -> {
                val uris = IntentCompat.getParcelableArrayListExtra(
                    intent,
                    Intent.EXTRA_STREAM,
                    Parcelable::class.java
                )
                uris?.let { newIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris) }
            }
        }
        newIntent.putIntegerArrayListExtra(
            Constants.CHAT_IDS_INTENT_KEY,
            ArrayList<Int>(chatIds)
        )
        newIntent.setClass(this, MainActivity::class.java)
        startActivity(newIntent)
        finish()
    }
}