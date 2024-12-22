package com.anshtya.jetx

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(MainActivityState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // TODO: fetch theme boolean from preferences
            flow { emit(true) }.collect { useDarkTheme ->
                _state.update {
                    it.copy(useDarkTheme = useDarkTheme)
                }
            }
        }
    }
}

data class MainActivityState(
    val useDarkTheme: Boolean? = null
)