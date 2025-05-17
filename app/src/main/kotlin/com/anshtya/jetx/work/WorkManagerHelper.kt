package com.anshtya.jetx.work

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.reflect.KClass

class WorkManagerHelper @Inject constructor(
    context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    fun buildConstraints(
        requiredNetworkType: NetworkType = NetworkType.CONNECTED,
        requiresStorageNotLow: Boolean = true,
        requiresBatteryNotLow: Boolean = false
    ): Constraints {
        val builder = Constraints.Builder()

        builder.setRequiredNetworkType(requiredNetworkType)
        if (requiresBatteryNotLow) builder.setRequiresBatteryNotLow(true)
        if (requiresStorageNotLow) builder.setRequiresStorageNotLow(true)

        return builder.build()
    }

    fun scheduleOneTimeWork(
        workerClass: KClass<out ListenableWorker>,
        uniqueWorkName: String,
        dataParams: Map<String, Any?>,
        existingWorkPolicy: ExistingWorkPolicy = ExistingWorkPolicy.KEEP,
        backoffPolicy: BackoffPolicy = BackoffPolicy.EXPONENTIAL,
        backoffDelay: Long = WorkRequest.MIN_BACKOFF_MILLIS,
        backoffTimeUnit: TimeUnit = TimeUnit.SECONDS,
        constraints: Constraints? = buildConstraints(),
        tags: Set<String> = emptySet()
    ) {
        val inputData = createInputData(dataParams)

        val workRequestBuilder = OneTimeWorkRequest.Builder(workerClass)
            .setInputData(inputData)
            .setBackoffCriteria(backoffPolicy, backoffDelay, backoffTimeUnit)

        constraints?.let { workRequestBuilder.setConstraints(it) }
        tags.forEach { tag -> workRequestBuilder.addTag(tag) }

        workManager.enqueueUniqueWork(
            uniqueWorkName,
            existingWorkPolicy,
            workRequestBuilder.build()
        )
    }

    fun cancelWorkByUniqueName(uniqueWorkName: String) {
        workManager.cancelUniqueWork(uniqueWorkName)
    }

    fun cancelWorkByTag(tag: String) {
        workManager.cancelAllWorkByTag(tag)
    }

    fun cancelAllWork() {
        workManager.cancelAllWork()
    }

    private fun createInputData(params: Map<String, Any?>): Data {
        val builder = Data.Builder()
        params.forEach { (key, value) ->
            when (value) {
                is String -> builder.putString(key, value)
                is UUID -> builder.putString(key, value.toString())
                is Int -> builder.putInt(key, value)
                is Long -> builder.putLong(key, value)
                is Float -> builder.putFloat(key, value)
                is Double -> builder.putDouble(key, value)
                is Boolean -> builder.putBoolean(key, value)

                null -> {}
            }
        }
        return builder.build()
    }
}