package com.tonyakitori.apps.messageforwarder.data.repository

import android.util.Log
import com.tonyakitori.apps.messageforwarder.data.models.SmsMessage
import com.tonyakitori.apps.messageforwarder.data.remote.TelegramClient
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SmsRepository(private val telegramClient: TelegramClient) {

    companion object {
        private const val MAX_RETRIES = 3
        private const val RETRY_DELAY_MS = 2000L
    }

    suspend fun forwardSmsToTelegram(
        sms: SmsMessage,
        botToken: String,
        chatId: String
    ): Result<Unit> {
        val formattedMessage = formatSmsMessage(sms)
        
        var lastException: Exception? = null
        repeat(MAX_RETRIES) { attempt ->
            val result = telegramClient.sendMessage(botToken, chatId, formattedMessage)
            
            if (result.isSuccess) {
                Log.d("SmsRepository", "SMS forwarded successfully on attempt ${attempt + 1}")
                return Result.success(Unit)
            } else {
                lastException = result.exceptionOrNull() as? Exception
                Log.w("SmsRepository", "Failed to forward SMS on attempt ${attempt + 1}: ${lastException?.message}")
                
                if (attempt < MAX_RETRIES - 1) {
                    delay(RETRY_DELAY_MS * (attempt + 1))
                }
            }
        }
        
        return Result.failure(lastException ?: Exception("Failed to forward SMS after $MAX_RETRIES attempts"))
    }

    suspend fun testConnection(botToken: String, chatId: String): Result<Unit> {
        val testMessage = "✅ Conexión exitosa!\n\nMessage Forwarder está configurado correctamente."
        val result = telegramClient.sendMessage(botToken, chatId, testMessage)
        
        return if (result.isSuccess) {
            Result.success(Unit)
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
        }
    }

    private fun formatSmsMessage(sms: SmsMessage): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val date = dateFormat.format(Date(sms.timestamp))

        val escapedMessage = escapeHtml(sms.message)
        val escapedSender = escapeHtml(sms.sender)
        
        return "📱 <b>Nuevo SMS Recibido</b>\n\n" +
               "<b>De:</b> $escapedSender\n" +
               "<b>Fecha:</b> $date\n\n" +
               "<b>Mensaje:</b>\n" +
               escapedMessage
    }
    
    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
    }
}
