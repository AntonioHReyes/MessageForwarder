package com.tonyakitori.apps.messageforwarder.data.models

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(markerClass = [InternalSerializationApi::class])
@Serializable
data class TelegramMessage(
    @SerialName("chat_id")
    val chatId: String,
    val text: String,
    @SerialName("parse_mode")
    val parseMode: String = "HTML"
)
