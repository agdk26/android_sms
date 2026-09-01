package com.alpha.alphasms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

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

        val database = AppDatabase.getInstance(context)

        CoroutineScope(Dispatchers.IO).launch {
            database.smsDao().insert(
                Sms(
                    sender = sender,
                    message = message,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}
