package com.anshtya.jetx.core.database.converter

import androidx.room.TypeConverter
import java.util.UUID

class UUIDConverter {
    @TypeConverter
    fun uuidToString(value: UUID): String {
        return value.toString()
    }
    @TypeConverter
    fun stringToUuid(value: String): UUID {
        return UUID.fromString(value)
    }
}