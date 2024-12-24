package com.anshtya.jetx.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.auth.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    authRepository: AuthRepository
) : ViewModel() {
    val state: StateFlow<HomeState> =
        authRepository.authStatus
            .map {
                HomeState(
                    authenticated = it.authCompleted,
                    profileCreated = it.profileCreated
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = HomeState()
            )
}

data class HomeState(
    val authenticated: Boolean? = null,
    val profileCreated: Boolean? = null
)