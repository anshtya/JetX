package com.anshtya.jetx.common.util

import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

const val FULL_DATE = "d MMMM yyyy"

fun ZonedDateTime.getDateOrTime(
    timePattern: String = "hh:mm a",
    datePattern: String = "dd/MM/yyyy",
    getTimeOnly: Boolean = false,
    getDateOnly: Boolean = false
): String {
    val zoneId = ZoneId.systemDefault()

    val messageTime = ZonedDateTime.ofInstant(this.toInstant(), zoneId)
    val now = ZonedDateTime.now(zoneId)
    val duration = Duration.between(messageTime, now)

    return if ((duration.toDays() < 1 || getTimeOnly) && !getDateOnly) {
        val timeFormatter = DateTimeFormatter.ofPattern(timePattern)
        messageTime.format(timeFormatter)
    } else {
        val dateFormatter = DateTimeFormatter.ofPattern(datePattern)
        messageTime.format(dateFormatter)
    }
}