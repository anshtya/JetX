package com.anshtya.jetx.calls.ui.navigation

import kotlinx.serialization.Serializable

sealed interface CallsDestinations {
    @Serializable
    data object CallLogs : CallsDestinations
}