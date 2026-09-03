package com.alpha.alphasms

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TelegramWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        val database = AppDatabase.getInstance(applicationContext)
        val storage = SecureStorage(applicationContext)

        val token = storage.getToken()
        val chatId = storage.getChatId()

        if (token.isNullOrBlank() || chatId.isNullOrBlank()) {
            return Result.failure()
        }

        val pendingSms = database.smsDao().getPending()

        for (sms in pendingSms) {

            try {
                val dateFormat =
                    SimpleDateFormat(
                        "yyyy.MM.dd HH:mm:ss",
                        Locale.getDefault()
                    )
                
                val receivedTime =
                    dateFormat.format(Date(sms.timestamp))
                
                val text =
                    "SMS from ${sms.sender}\n" +
                    "Received: $receivedTime\n\n" +
                    sms.message

                val url = URL(
                    "https://api.telegram.org/bot$token/sendMessage"
                )

                val connection =
                    url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                connection.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
                )

                val postData =
                    "chat_id=${URLEncoder.encode(chatId, "UTF-8")}" +
                    "&text=${URLEncoder.encode(text, "UTF-8")}"

                connection.outputStream.use { output ->
                    output.write(postData.toByteArray())
                }

                val responseCode = connection.responseCode

                connection.disconnect()

                if (responseCode in 200..299) {
                    database.smsDao().updateTelegramStatus(
                        sms.id,
                        "SENT"
                    )
                }
            } catch (e: Exception) {
                // Leave the SMS as PENDING.
                // WorkManager can retry the work later.
                return Result.retry()
            }
        }

        return Result.success()
    }
}
