package com.tonyakitori.apps.messageforwarder.data.models

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
@OptIn(markerClass = [InternalSerializationApi::class])
@Serializable
data class TelegramResponse(
    val ok: Boolean,
    val description: String? = null
)
