package com.anshtya.jetx.notifications

object NotificationIds {
    const val MESSAGE_SUMMARY = 1

    fun getMessageNotificationId(chatId: Int): Int {
        return MESSAGE_SUMMARY + chatId
    }
}