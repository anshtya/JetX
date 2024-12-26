package com.anshtya.jetx.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.auth.data.AuthRepository
import com.anshtya.jetx.auth.data.model.AuthStatus
import com.anshtya.jetx.profile.ProfileRepository
import com.anshtya.jetx.profile.model.ProfileStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    authRepository: AuthRepository,
    profileRepository: ProfileRepository
) : ViewModel() {
    val state: StateFlow<HomeState> =
        combine(
            authRepository.authStatus,
            profileRepository.profileStatus
        ) { authStatus, profileStatus ->
            val authenticated = when (authStatus) {
                AuthStatus.AUTHORIZED -> true
                AuthStatus.UNAUTHORIZED -> false
                else -> null
            }

            val profileCreated = when (profileStatus) {
                ProfileStatus.CREATED -> true
                ProfileStatus.NOT_CREATED -> false
            }

            HomeState(
                authenticated = authenticated,
                profileCreated = profileCreated
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = HomeState()
        )
}

data class HomeState(
    val authenticated: Boolean? = null,
    val profileCreated: Boolean? = null
)