package com.anshtya.jetx.work.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.anshtya.jetx.core.network.service.UserProfileService
import com.anshtya.jetx.core.network.util.toResult
import com.anshtya.jetx.core.preferences.JetxPreferencesStore
import com.anshtya.jetx.fcm.FcmTokenManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailabilityLight
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class FcmRefreshWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val store: JetxPreferencesStore,
    private val fcmTokenManager: FcmTokenManager,
    private val userProfileService: UserProfileService
) : CoroutineWorker(context, workerParameters) {
    private val tag = this::class.simpleName

    override suspend fun doWork(): Result {
        return try {
            val fcmAvailable = GoogleApiAvailabilityLight.getInstance()
                .isGooglePlayServicesAvailable(context)

            if (fcmAvailable != ConnectionResult.SUCCESS) {
                Log.i(tag, "FCM unavailable. Skipping refresh.")
            } else {
                val userId = store.account.getUserId()
                val oldToken = store.account.getFcmToken()
                val newToken = fcmTokenManager.getToken()

                oldToken?.let {
                    if (userId != null && oldToken != newToken) {
                        Log.i(tag, "Uploading new FCM token..")
                        userProfileService.updateFcmToken(newToken)
                            .toResult().getOrThrow()
                        store.account.storeFcmToken(newToken)
                    } else {
                        Log.i(tag, "Skipping refresh")
                    }
                } ?: Log.i(tag, "FCM token not found. Skipping refresh.")
            }

            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        fun scheduleWork(workManager: WorkManager) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequest.Builder(FcmRefreshWorker::class)
                .setConstraints(constraints)
                .build()

            workManager.enqueueUniqueWork(
                "fcm_refresh",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }
}