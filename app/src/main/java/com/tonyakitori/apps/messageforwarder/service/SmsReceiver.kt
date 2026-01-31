package com.tonyakitori.apps.messageforwarder.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log

class SmsReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "SmsReceiver"
        const val EXTRA_SMS_SENDER = "sms_sender"
        const val EXTRA_SMS_MESSAGE = "sms_message"
        const val EXTRA_SMS_TIMESTAMP = "sms_timestamp"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            
            if (messages.isEmpty()) {
                Log.w(TAG, "No SMS messages found in intent")
                return
            }

            // Join all messages into a single string
            val sender = messages[0].displayOriginatingAddress
            val messageBody = messages.joinToString("") { it.messageBody ?: "" }
            val timestamp = messages[0].timestampMillis

            Log.d(TAG, "SMS received from: $sender")

            val serviceIntent = Intent(context, SmsForwarderService::class.java).apply {
                action = SmsForwarderService.ACTION_FORWARD_SMS
                putExtra(EXTRA_SMS_SENDER, sender)
                putExtra(EXTRA_SMS_MESSAGE, messageBody)
                putExtra(EXTRA_SMS_TIMESTAMP, timestamp)
            }

            context.startForegroundService(serviceIntent)
        }
    }
}
