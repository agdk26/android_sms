package com.alpha.alphasms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

class SmsReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SMS_RECEIVED =
            "com.alpha.alphasms.SMS_RECEIVED"

        const val EXTRA_SENDER =
            "sender"

        const val EXTRA_MESSAGE =
            "message"
    }

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            return
        }

        val messages =
            Telephony.Sms.Intents.getMessagesFromIntent(intent)

        if (messages.isEmpty()) {
            return
        }

        val sender =
            messages.first().displayOriginatingAddress
                ?: "Unknown"

        val message =
            messages.joinToString("") {
                it.messageBody ?: ""
            }

        val resultIntent = Intent(
            ACTION_SMS_RECEIVED
        ).apply {

            setPackage(context.packageName)

            putExtra(
                EXTRA_SENDER,
                sender
            )

            putExtra(
                EXTRA_MESSAGE,
                message
            )
        }

        context.sendBroadcast(resultIntent)
    }
}
