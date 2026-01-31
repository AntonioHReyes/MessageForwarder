package com.tonyakitori.apps.messageforwarder.data.models

data class SmsMessage(
    val sender: String,
    val message: String,
    val timestamp: Long,
    val forwarded: Boolean = false // to use in future with resend logic
)
