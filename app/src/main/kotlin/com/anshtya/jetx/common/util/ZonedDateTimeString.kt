package com.anshtya.jetx.common.util

import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun ZonedDateTime.formattedString(): String {
    val zoneId = ZoneId.systemDefault()

    val messageTime = ZonedDateTime.ofInstant(this.toInstant(), zoneId)
    val now = ZonedDateTime.now(zoneId)
    val duration = Duration.between(messageTime, now)

    return if (duration.toDays() < 1) {
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
        messageTime.format(timeFormatter)
    } else {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        messageTime.format(dateFormatter)
    }
}