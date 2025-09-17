package com.anshtya.jetx.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.core.preferences.PreferencesStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferencesStore: PreferencesStore
): ViewModel() {
    fun onOnboardingComplete() {
        viewModelScope.launch {
            preferencesStore.setOnboarded()
        }
    }
}