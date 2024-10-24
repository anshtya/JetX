package com.anshtya.jetx.ui.features.auth.signin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.data.model.Result
import com.anshtya.jetx.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    init {
        viewModelScope.launch {
            delay(3000L)

            val result = authRepository.login(username = "ansh", password = "ansh")
            when (result) {
                is Result.Success -> {
                    Log.d("foo", "done")
                }

                is Result.Error -> {
                    Log.d("foo", result.errorMessage ?: "shit")
                }
            }
        }
    }
}