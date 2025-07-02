package com.anshtya.jetx.camera

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.IntentCompat
import com.anshtya.jetx.attachments.ui.preview.MediaPreviewActivity
import com.anshtya.jetx.camera.ui.CameraScreen
import com.anshtya.jetx.ui.theme.JetXTheme
import com.anshtya.jetx.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class CameraActivity : ComponentActivity() {
    lateinit var recipientId: UUID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.BLACK))

        val intentRecipientId = IntentCompat.getSerializableExtra(
            intent,
            Constants.RECIPIENT_INTENT_KEY,
            UUID::class.java
        )
        intentRecipientId?.let { recipientId = it } ?: finish()

        setContent {
            JetXTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier
                        .safeDrawingPadding()
                        .fillMaxSize()
                ) {
                    CameraScreen(
                        onBackClick = ::finish,
                        onNavigateToPreview = { mediaUri ->
                            val intent = Intent(this, MediaPreviewActivity::class.java).apply {
                                putExtra(Intent.EXTRA_STREAM, ArrayList(listOf(mediaUri)))
                                putExtra(Constants.RECIPIENT_INTENT_KEY, recipientId)
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}