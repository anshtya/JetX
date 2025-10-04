package com.anshtya.jetx.auth.data

import android.util.Log
import com.anshtya.jetx.auth.data.model.AuthState
import com.anshtya.jetx.core.coroutine.DefaultScope
import com.anshtya.jetx.core.database.dao.UserProfileDao
import com.anshtya.jetx.core.database.entity.UserProfileEntity
import com.anshtya.jetx.core.network.service.AuthService
import com.anshtya.jetx.core.network.service.UserProfileService
import com.anshtya.jetx.core.network.util.toResult
import com.anshtya.jetx.core.preferences.PreferencesStore
import com.anshtya.jetx.core.preferences.TokenStore
import com.anshtya.jetx.fcm.FcmTokenManager
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val userProfileService: UserProfileService,
    private val fcmTokenManager: FcmTokenManager,
    private val tokenStore: TokenStore,
    private val preferencesStore: PreferencesStore,
    private val userProfileDao: UserProfileDao,
    private val logoutManager: LogoutManager,
    @DefaultScope scope: CoroutineScope
) : AuthRepository {
    private val tag = this::class.simpleName

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initializing)
    override val authState = _authState.asStateFlow()

    init {
        scope.launch { initialAuth() }
    }

    private suspend fun initialAuth() {
        val authToken = tokenStore.getAuthToken()
        if (authToken.accessToken == null || authToken.refreshToken == null) {
            _authState.update { AuthState.Unauthenticated }
            return
        }

        authService.refreshToken(authToken.refreshToken)
            .toResult()
            .onSuccess {
                tokenStore.storeAuthToken(
                    userId = it.userId,
                    access = it.accessToken,
                    refresh = it.refreshToken
                )
            }.onFailure { throwable ->
                Log.e(tag, throwable.message, throwable)
            }
        val userId = tokenStore.getUserId()!!
        _authState.update { AuthState.Authenticated(userId) }
    }

    override suspend fun login(
        phoneNumber: String,
        pin: String
    ): Result<Unit> =
        runCatching {
            val authResponse = authService.login(
                phoneNumber = phoneNumber,
                pin = pin,
                fcmToken = fcmTokenManager.getToken()
            )
                .toResult()
                .getOrElse {
                    Log.e(tag, it.message, it)
                    return Result.failure(it)
                }

            tokenStore.storeAuthToken(
                userId = authResponse.userId,
                access = authResponse.accessToken,
                refresh = authResponse.refreshToken
            )

            val userProfile = userProfileService.getProfileById(authResponse.userId)
                .toResult()
                .getOrElse {
                    Log.e(tag, it.message, it)
                    return Result.failure(it)
                }
            userProfileDao.upsertUserProfile(
                UserProfileEntity(
                    id = UUID.fromString(authResponse.userId),
                    name = userProfile.displayName,
                    username = userProfile.username,
                    profilePicture = null // TODO: manage profile picture at server
                )
            )
            preferencesStore.setProfileCreated()

            _authState.update { AuthState.Authenticated(authResponse.userId) }
        }

    override suspend fun register(
        phoneNumber: String,
        pin: String
    ): Result<Unit> {
        return authService.register(phoneNumber, pin)
            .toResult()
            .onSuccess { authResponse ->
                tokenStore.storeAuthToken(
                    userId = authResponse.userId,
                    access = authResponse.accessToken,
                    refresh = authResponse.refreshToken
                )
                _authState.update { AuthState.Authenticated(authResponse.userId) }
            }
            .onFailure {
                Log.e(tag, it.message, it)
            }
            .map {}
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
            val token = tokenStore.getToken(TokenStore.ACCESS_TOKEN)!!
            authService.logoutUser(token)
                .toResult()
                .onFailure {
                    Log.e(tag, it.message, it)
                    return Result.failure(it)
                }

            logoutManager.performLocalCleanup().onFailure {
                Log.e(tag, it.message, it)
                return Result.failure(it)
            }
            _authState.update { AuthState.Unauthenticated }
        }
}