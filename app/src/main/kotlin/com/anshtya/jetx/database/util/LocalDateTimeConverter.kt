package com.anshtya.jetx.database.util

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class LocalDateTimeConverter {
    @TypeConverter
    fun longToLocalDateTime(value: Long): LocalDateTime {
        val instant = Instant.ofEpochMilli(value)
        return LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
    }

    @TypeConverter
    fun localDateTimeToLong(value: LocalDateTime): Long {
        return value.toInstant(ZoneOffset.UTC).toEpochMilli()
    }
}