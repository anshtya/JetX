package com.anshtya.jetx.work.util

import androidx.work.Data
import java.util.UUID

fun createInputData(params: Map<String, Any>): Data {
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
        }
    }
    return builder.build()
}