package com.anshtya.jetx.onboarding.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface OnboardingDestination {
    @Serializable
    data object Entry : OnboardingDestination

    @Serializable
    data object RequestPermission : OnboardingDestination
}