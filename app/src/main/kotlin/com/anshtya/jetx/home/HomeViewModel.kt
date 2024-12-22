package com.anshtya.jetx.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        monitorAuthStatus()
    }

    private fun monitorAuthStatus() {
        viewModelScope.launch {
            // TODO: collect auth status

            _state.update {
                it.copy(
                    authenticated = true,
                    profileCreated = true
                )
            }
        }
    }
}

data class HomeState(
    val authenticated: Boolean? = null,
    val profileCreated: Boolean? = null
)