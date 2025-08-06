package com.anshtya.jetx.send

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.IntentCompat
import com.anshtya.jetx.MainActivity
import com.anshtya.jetx.auth.data.AuthRepository
import com.anshtya.jetx.ui.theme.JetXTheme
import com.anshtya.jetx.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SendActivity : ComponentActivity() {
    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            JetXTheme {
                SendScreen(
                    onNavigateUp = this::finish,
                    onActivityFinish = {
                        val intent = Intent(this@SendActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    onSend = { chatIds ->
                        createSendIntent(intent, chatIds)
                    }
                )
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