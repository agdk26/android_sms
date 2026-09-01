package com.alpha.alphasms

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SecureStorage(private val context: Context) {

    companion object {
        private const val KEYSTORE_NAME = "AndroidKeyStore"
        private const val KEY_ALIAS = "AlphaSmsConfigKey"

        private const val PREFS_NAME = "alpha_sms_config"

        private const val TOKEN_KEY = "bot_token"
        private const val CHAT_ID_KEY = "chat_id"
    }

    init {
        createKeyIfNeeded()
    }

    private fun createKeyIfNeeded() {

        val keyStore = KeyStore.getInstance(KEYSTORE_NAME)
        keyStore.load(null)

        if (!keyStore.containsAlias(KEY_ALIAS)) {

            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                KEYSTORE_NAME
            )

            val keySpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or
                        KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(
                    KeyProperties.ENCRYPTION_PADDING_NONE
                )
                .setKeySize(256)
                .build()

            keyGenerator.init(keySpec)
            keyGenerator.generateKey()
        }
    }

    private fun getKey(): SecretKey {

        val keyStore = KeyStore.getInstance(KEYSTORE_NAME)
        keyStore.load(null)

        return keyStore.getKey(
            KEY_ALIAS,
            null
        ) as SecretKey
    }

    private fun encrypt(value: String): String {

        val cipher = Cipher.getInstance(
            "AES/GCM/NoPadding"
        )

        cipher.init(
            Cipher.ENCRYPT_MODE,
            getKey()
        )

        val encrypted = cipher.doFinal(
            value.toByteArray(
                StandardCharsets.UTF_8
            )
        )

        val iv = cipher.iv

        // Store IV together with encrypted data.
        val result = iv + encrypted

        return Base64.encodeToString(
            result,
            Base64.NO_WRAP
        )
    }

    private fun decrypt(value: String): String {

        val data = Base64.decode(
            value,
            Base64.NO_WRAP
        )

        val iv = data.copyOfRange(
            0,
            12
        )

        val encrypted = data.copyOfRange(
            12,
            data.size
        )

        val cipher = Cipher.getInstance(
            "AES/GCM/NoPadding"
        )

        val spec = GCMParameterSpec(
            128,
            iv
        )

        cipher.init(
            Cipher.DECRYPT_MODE,
            getKey(),
            spec
        )

        return String(
            cipher.doFinal(encrypted),
            StandardCharsets.UTF_8
        )
    }

    fun saveToken(token: String) {

        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .putString(
                TOKEN_KEY,
                encrypt(token)
            )
            .apply()
    }

    fun saveChatId(chatId: String) {

        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .putString(
                CHAT_ID_KEY,
                encrypt(chatId)
            )
            .apply()
    }

    fun getToken(): String? {

        val encrypted = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
            .getString(
                TOKEN_KEY,
                null
            )
            ?: return null

        return decrypt(encrypted)
    }

    fun getChatId(): String? {

        val encrypted = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
            .getString(
                CHAT_ID_KEY,
                null
            )
            ?: return null

        return decrypt(encrypted)
    }

    fun delete() {

        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .clear()
            .apply()
    }
}
