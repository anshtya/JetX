package com.anshtya.jetx.chats.ui.navigation.navtype

import android.net.Uri
import android.os.Bundle
import androidx.core.os.BundleCompat
import androidx.navigation.NavType
import com.anshtya.jetx.chats.ui.chat.ChatUserArgs
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val chatUserArgsType = object : NavType<ChatUserArgs>(
    isNullableAllowed = false
) {
    val json = Json

    override fun get(bundle: Bundle, key: String): ChatUserArgs? {
        return BundleCompat.getParcelable(bundle, key, ChatUserArgs::class.java)
    }

    override fun parseValue(value: String): ChatUserArgs {
        return json.decodeFromString<ChatUserArgs>(Uri.decode(value))
    }

    override fun put(bundle: Bundle, key: String, value: ChatUserArgs) {
        bundle.putParcelable(key, value)
    }

    override fun serializeAsValue(value: ChatUserArgs): String {
        return Uri.encode(json.encodeToString<ChatUserArgs>(value))
    }
}