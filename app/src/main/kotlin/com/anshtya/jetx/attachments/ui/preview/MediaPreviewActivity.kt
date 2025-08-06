package com.anshtya.jetx.attachments.ui.preview

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.IntentCompat
import com.anshtya.jetx.MainActivity
import com.anshtya.jetx.ui.theme.JetXTheme
import com.anshtya.jetx.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class MediaPreviewActivity : ComponentActivity() {
    private val viewModel: MediaPreviewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntent(intent)

        setContent {
            JetXTheme(darkTheme = true) {
                Surface(Modifier.fillMaxSize()) {
                    MediaPreviewRoute(
                        onBackClick = ::finish,
                        navigateToChat = {
                            val intent = Intent(this, MainActivity::class.java).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            }
                            startActivity(intent)
                        },
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val uris = IntentCompat.getParcelableArrayListExtra(
            intent,
            Intent.EXTRA_STREAM,
            Parcelable::class.java
        )?.map { parcelable -> parcelable as Uri }
        val chatIds = intent.getIntegerArrayListExtra(Constants.CHAT_IDS_INTENT_KEY)
        val recipientId = IntentCompat.getSerializableExtra(
            intent,
            Constants.RECIPIENT_INTENT_KEY,
            UUID::class.java
        )

        if (uris != null && (chatIds != null || recipientId != null)) {
            viewModel.processIncomingData(chatIds, recipientId,  uris)
        } else {
            finish()
        }
    }
}