package com.anshtya.jetx.chats.ui.chatlist

import androidx.annotation.StringRes
import com.anshtya.jetx.R

enum class FilterOption(
    @StringRes val displayName: Int
) {
    ALL(displayName = R.string.filter_all),
    UNREAD(displayName = R.string.filter_unread),
    FAVORITES(displayName = R.string.filter_favorites)
}