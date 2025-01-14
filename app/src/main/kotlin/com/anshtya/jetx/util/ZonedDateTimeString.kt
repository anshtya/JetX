package com.anshtya.jetx.util

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun ZonedDateTime.getDateOrTime(
    timePattern: String = "h:mm a",
    datePattern: String = "dd/MM/yyyy",
    getTimeOnly: Boolean = false,
    getDateOnly: Boolean = false,
    getToday: Boolean = false,
    getYesterday: Boolean = false,
): String {
    val messageLocalTime = this.withZoneSameInstant(ZoneId.systemDefault())

    return when {
        getTimeOnly -> messageLocalTime.getTime(timePattern)
        getDateOnly -> messageLocalTime.getDate(datePattern)
        else -> {
            val nowDate = ZonedDateTime.now().toLocalDate()
            val messageDate = messageLocalTime.toLocalDate()

            if (nowDate.isEqual(messageDate)) {
                if (getToday) "Today" else messageLocalTime.getTime(timePattern)
            } else if (messageDate.isEqual(nowDate.minusDays(1)) && getYesterday) "Yesterday"
            else messageLocalTime.getDate(datePattern)
        }
    }
}

private fun ZonedDateTime.getTime(timePattern: String): String {
    return this.format(DateTimeFormatter.ofPattern(timePattern))
        .replace("AM", "am")
        .replace("PM", "pm")
}

private fun ZonedDateTime.getDate(datePattern: String): String {
    return this.format(DateTimeFormatter.ofPattern(datePattern))
}