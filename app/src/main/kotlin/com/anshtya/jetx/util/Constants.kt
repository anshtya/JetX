package com.anshtya.jetx.util

import androidx.compose.ui.unit.dp

object Constants {
    // Supabase
    const val ATTACHMENT_TABLE = "attachment"
    const val MEDIA_STORAGE = "media"
    const val MESSAGE_TABLE = "messages"
    const val PROFILE_TABLE = "profile"

    val defaultPadding = 10.dp

    // Deeplink
    const val APP_HOST = "jetx.anshtya.com"
    const val BASE_APP_URL = "https://$APP_HOST"
    const val CHAT_ARG = "chat"
    const val CHAT_ID_ARG = "chatId"
    const val CHAT_TITLE = "chatTitle"

    // Intent
    const val RECIPIENT_INTENT_KEY = "recipient"
    const val CHAT_IDS_INTENT_KEY = "chats"
}