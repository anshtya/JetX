package com.anshtya.jetx.chats.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class ChatsBaseViewModel: ViewModel() {
    protected val mutableSelectedChats = MutableStateFlow<Set<Int>>(emptySet())
    val selectedChats = mutableSelectedChats.asStateFlow()

    fun selectChat(id: Int) {
        mutableSelectedChats.update {
            it.toMutableSet().apply { add(id) }
        }
    }

    fun unselectChat(id: Int) {
        mutableSelectedChats.update {
            it.toMutableSet().apply { remove(id) }
        }
    }

    fun clearSelectedChats() {
        mutableSelectedChats.update { emptySet() }
    }
}