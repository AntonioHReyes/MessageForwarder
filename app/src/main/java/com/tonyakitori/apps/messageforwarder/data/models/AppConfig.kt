package com.tonyakitori.apps.messageforwarder.data.models

data class AppConfig(
    val botToken: String = "",
    val chatId: String = "",
    val serviceEnabled: Boolean = false
) {
    fun isConfigured(): Boolean {
        return botToken.isNotBlank() && chatId.isNotBlank()
    }
}
