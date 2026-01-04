package com.anshtya.jetx.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun convertTo12HourTime(millis: Long): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    return formatter.format(Instant.ofEpochMilli(millis))
}