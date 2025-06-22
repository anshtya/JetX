package com.anshtya.jetx.attachments.ui.preview

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.IntentCompat
import com.anshtya.jetx.ui.theme.JetXTheme
import com.anshtya.jetx.util.Constants

class MediaPreviewActivity : ComponentActivity() {
    private val viewModel: MediaPreviewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        enableEdgeToEdge()
        setContent {
            JetXTheme(darkTheme = true) {
                MediaPreviewRoute(
                    navigateToChat = ::finish,
                    viewModel = viewModel
                )
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
        uris?.let { viewModel.addUris(uris) }
        val recipients = intent.getIntegerArrayListExtra(Constants.RECIPIENTS_INTENT_KEY)
        recipients?.let { viewModel.addRecipients(recipients.toList()) }
    }
}