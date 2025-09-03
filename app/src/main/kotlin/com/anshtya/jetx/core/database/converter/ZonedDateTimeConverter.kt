package com.anshtya.jetx.core.database.converter

import androidx.room.TypeConverter
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ZonedDateTimeConverter {
    @TypeConverter
    fun longToZonedDateTime(value: Long): ZonedDateTime {
        val instant = Instant.ofEpochMilli(value)
        return ZonedDateTime.ofInstant(instant, ZoneId.of("UTC"))
    }

    @TypeConverter
    fun zonedDateTimeToLong(value: ZonedDateTime): Long {
        return value.toInstant().toEpochMilli()
    }
}