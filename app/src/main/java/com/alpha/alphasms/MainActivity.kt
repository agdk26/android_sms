package com.alpha.alphasms

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.alpha.alphasms.ui.theme.AlphaSmsTheme
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class MainActivity : ComponentActivity() {

    private lateinit var storage: SecureStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = SecureStorage(this)

        setContent {

            AlphaSmsTheme {

                TelegramSettingsScreen()
            }
        }
    }

    @Composable
    private fun TelegramSettingsScreen() {

        var token by remember {
            mutableStateOf(
                storage.getToken() ?: ""
            )
        }

        var chatId by remember {
            mutableStateOf(
                storage.getChatId() ?: ""
            )
        }

        var status by remember {

            mutableStateOf(

                if (
                    token.isNotEmpty() &&
                    chatId.isNotEmpty()
                ) {
                    "Settings loaded"
                } else {
                    "Settings not configured"
                }
            )
        }
        
        var showDeleteDialog by remember {
            mutableStateOf(false)
        }

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "AlphaSms"
            )

            OutlinedTextField(

                value = token,

                onValueChange = {
                    token = it
                },

                modifier = Modifier.fillMaxWidth(),

                label = {
                    Text("Bot Token")
                },

                visualTransformation =
                    PasswordVisualTransformation(),

                singleLine = true
            )

            OutlinedTextField(

                value = chatId,

                onValueChange = {
                    chatId = it
                },

                modifier = Modifier.fillMaxWidth(),

                label = {
                    Text("Chat ID")
                },

                singleLine = true
            )

            Button(

                onClick = {

                    if (token.isBlank()) {

                        showToast(
                            "Enter Bot Token"
                        )

                        return@Button
                    }

                    if (chatId.isBlank()) {

                        showToast(
                            "Enter Chat ID"
                        )

                        return@Button
                    }

                    storage.saveToken(
                        token.trim()
                    )

                    storage.saveChatId(
                        chatId.trim()
                    )

                    status =
                        "Settings saved"

                    showToast(
                        "Settings saved"
                    )
                },

                modifier = Modifier.fillMaxWidth()
            ) {

                Text("Save")
            }

            Button(

                onClick = {

                    sendTestMessage(
                        token = token.trim(),
                        chatId = chatId.trim()
                    )
                },

                modifier = Modifier.fillMaxWidth()
            ) {

                Text("Send Test Message")
            }

            TextButton(

                onClick = {
                    showDeleteDialog = true
                },

                modifier = Modifier.fillMaxWidth()
            ) {

                Text("Delete Settings")
            }

            Text(
                text = status
            )
        }
        
        if (showDeleteDialog) {
        
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                },
        
                title = {
                    Text("Delete Settings")
                },
        
                text = {
                    Text("Are you sure you want to delete settings?")
                },
        
                confirmButton = {
                    TextButton(
                        onClick = {
        
                            storage.delete()
        
                            token = ""
                            chatId = ""
        
                            status = "Settings deleted"
        
                            showDeleteDialog = false
        
                            showToast("Settings deleted")
                        }
                    ) {
                        Text("Yes")
                    }
                },
        
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                        }
                    ) {
                        Text("No")
                    }
                }
            )
        }
    }

    private fun sendTestMessage(
        token: String,
        chatId: String
    ) {

        if (
            token.isBlank() ||
            chatId.isBlank()
        ) {

            showToast(
                "Enter Bot Token and Chat ID first"
            )

            return
        }

        Thread {

            try {

                val url =
                    "https://api.telegram.org/bot$token/sendMessage"

                val params =
                    "chat_id=" +
                            URLEncoder.encode(
                                chatId,
                                "UTF-8"
                            ) +
                            "&text=" +
                            URLEncoder.encode(
                                "Test message from AlphaSms",
                                "UTF-8"
                            )

                val connection =
                    URL(url)
                        .openConnection()
                            as HttpURLConnection

                connection.requestMethod =
                    "POST"

                connection.doOutput =
                    true

                connection.connectTimeout =
                    10000

                connection.readTimeout =
                    10000

                connection.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
                )

                connection.outputStream.use { output ->

                    output.write(
                        params.toByteArray(
                            Charsets.UTF_8
                        )
                    )
                }

                val responseCode =
                    connection.responseCode

                connection.disconnect()

                runOnUiThread {

                    if (responseCode == 200) {

                        showToast(
                            "Test message sent"
                        )

                    } else {

                        showToast(
                            "Telegram error: HTTP $responseCode"
                        )
                    }
                }

            } catch (e: Exception) {

                runOnUiThread {

                    showToast(
                        "Error: ${e.message}"
                    )
                }
            }

        }.start()
    }

    private fun showToast(
        message: String
    ) {

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}
