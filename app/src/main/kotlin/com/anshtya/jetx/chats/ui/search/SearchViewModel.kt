package com.anshtya.jetx.chats.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.common.model.UserProfile
import com.anshtya.jetx.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _navigateToChat = MutableStateFlow(false)
    val navigateToChat = _navigateToChat.asStateFlow()

    var chatId: Int = 0
        private set

    val searchSuggestions: StateFlow<List<UserProfile>> = _searchQuery
        .debounce(500L)
        .mapLatest {
            if (it.isBlank()) {
                emptyList()
            } else {
                profileRepository.searchProfiles(it)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun onProfileClick(userProfile: UserProfile) {
        viewModelScope.launch {
            // TODO: navigate to chat or create chat
//            _navigateToChat.update { true }
        }
    }

    fun changeSearchQuery(searchQuery: String) {
        _searchQuery.update { searchQuery }
    }

    fun clearSearch() {
        _searchQuery.update { "" }
    }
}