package com.anshtya.jetx.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.auth.data.AuthRepository
import com.anshtya.jetx.auth.data.model.AuthStatus
import com.anshtya.jetx.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    authRepository: AuthRepository,
    profileRepository: ProfileRepository
) : ViewModel() {
    val userState: StateFlow<UserState?> = authRepository.authStatus
        .map {
            when (it) {
                AuthStatus.AUTHORIZED -> {
                    UserState(
                        authenticated = true,
                        profileCreated = profileRepository.profileStatus.first()
                    )
                }
                AuthStatus.UNAUTHORIZED -> {
                    UserState(
                        authenticated = false,
                        profileCreated = false
                    )
                }
                else -> null
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )
}

data class UserState(
    val authenticated: Boolean,
    val profileCreated: Boolean
)