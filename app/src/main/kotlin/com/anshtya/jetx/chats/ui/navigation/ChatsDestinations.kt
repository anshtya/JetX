package com.anshtya.jetx.chats.ui.navigation

import kotlinx.serialization.Serializable

sealed interface ChatsDestinations {
    @Serializable
    data object ChatList : ChatsDestinations

    @Serializable
    data class Chat(val id: Int) : ChatsDestinations
}