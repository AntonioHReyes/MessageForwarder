package com.tonyakitori.apps.messageforwarder.data.remote

import android.util.Log
import com.tonyakitori.apps.messageforwarder.data.models.TelegramMessage
import com.tonyakitori.apps.messageforwarder.data.models.TelegramResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class TelegramClient {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("TelegramClient", message)
                }
            }
            level = LogLevel.INFO
        }

        engine {
            connectTimeout = 15_000
            socketTimeout = 15_000
        }
    }

    suspend fun sendMessage(botToken: String, chatId: String, text: String): Result<TelegramResponse> {
        return try {
            val message = TelegramMessage(
                chatId = chatId,
                text = text
            )

            val response: TelegramResponse = client.post("https://api.telegram.org/bot$botToken/sendMessage") {
                contentType(ContentType.Application.Json)
                setBody(message)
            }.body()

            if (response.ok) {
                Result.success(response)
            } else {
                Result.failure(Exception("Telegram API error: ${response.description}"))
            }
        } catch (e: Exception) {
            Log.e("TelegramClient", "Error sending message", e)
            Result.failure(e)
        }
    }

    fun close() {
        client.close()
    }
}
