package com.anshtya.jetx.ui.features.auth.signup

import androidx.lifecycle.ViewModel
import com.anshtya.jetx.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

}