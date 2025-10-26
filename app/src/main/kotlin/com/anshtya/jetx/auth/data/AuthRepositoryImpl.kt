package com.anshtya.jetx.auth.data

import android.util.Log
import com.anshtya.jetx.core.network.service.AuthService
import com.anshtya.jetx.core.network.util.toResult
import com.anshtya.jetx.core.preferences.JetxPreferencesStore
import com.anshtya.jetx.fcm.FcmTokenManager
import com.anshtya.jetx.profile.data.ProfileRepository
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authService: AuthService,
    private val fcmTokenManager: FcmTokenManager,
    private val authManager: AuthManager,
    private val store: JetxPreferencesStore,
    private val logoutManager: LogoutManager,
) : AuthRepository {
    private val tag = this::class.simpleName

    override suspend fun login(
        phoneNumber: String,
        pin: String
    ): Result<Unit> = runCatching {
        val fcmToken = fcmTokenManager.getToken()

        val authResponse = authService.login(
            phoneNumber = phoneNumber,
            pin = pin,
            fcmToken = fcmToken
        )
            .toResult()
            .getOrElse {
                Log.e(tag, it.message, it)
                return Result.failure(it)
            }

        authManager.storeSession(
            userId = authResponse.userId,
            accessToken = authResponse.accessToken,
            refreshToken = authResponse.refreshToken,
            autoUpdate = false
        )

        profileRepository.fetchAndSaveProfile(authResponse.userId).onFailure {
            Log.e(tag, "Failed to fetch profile: ${authResponse.userId}", it)
            return Result.failure(it)
        }

        store.account.storeFcmToken(fcmToken)
        store.user.setProfileCreated()

        authManager.setSessionFromStorage()
    }

    override suspend fun register(
        phoneNumber: String,
        pin: String
    ): Result<Unit> {
        return authService.register(phoneNumber, pin)
            .toResult()
            .onSuccess { authResponse ->
                authManager.storeSession(
                    userId = authResponse.userId,
                    accessToken = authResponse.accessToken,
                    refreshToken = authResponse.refreshToken
                )
            }.onFailure {
                Log.e(tag, it.message, it)
            }.map {}
    }

    /**
     * Checks if a user exists for the given phone number and country code.
     *
     * Implementation detail: Validates the phone number format using [PhoneNumberUtil]
     * before performing a network call to check if the user exists.
     */
    override suspend fun checkUser(
        number: Long,
        countryCode: Int
    ): Result<Boolean> {
        val phone = PhoneNumberUtil.getInstance()

        val phoneNumber = Phonenumber.PhoneNumber()
            .setCountryCode(countryCode)
            .setNationalNumber(number)
        val validNumber = phone.isValidNumber(phoneNumber)
        if (!validNumber) {
            return Result.failure(Exception("Invalid phone number."))
        }

        return authService.checkUser(
            phoneNumber = phone.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164)
        )
            .toResult()
            .map { it.exists }
            .onFailure {
                Log.e(tag, it.message, it)
            }
    }

    override suspend fun logout(): Result<Unit> =
        runCatching {
            val token = authManager.authState.value.currentAccessTokenOrNull()!!
            authService.logoutUser(token)
                .toResult()
                .onFailure {
                    Log.e(tag, it.message, it)
                    return Result.failure(it)
                }

            authManager.deleteSession()

            withContext(NonCancellable) {
                logoutManager.performLocalCleanup().onFailure {
                    Log.e(tag, "Failed to clear local user data", it)
                }
            }
        }
}