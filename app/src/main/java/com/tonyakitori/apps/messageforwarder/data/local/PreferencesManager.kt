package com.tonyakitori.apps.messageforwarder.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tonyakitori.apps.messageforwarder.data.models.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    
    companion object {
        private val BOT_TOKEN_KEY = stringPreferencesKey("bot_token")
        private val CHAT_ID_KEY = stringPreferencesKey("chat_id")
        private val SERVICE_ENABLED_KEY = booleanPreferencesKey("service_enabled")
    }

    val configFlow: Flow<AppConfig> = context.dataStore.data.map { preferences ->
        AppConfig(
            botToken = preferences[BOT_TOKEN_KEY] ?: "",
            chatId = preferences[CHAT_ID_KEY] ?: "",
            serviceEnabled = preferences[SERVICE_ENABLED_KEY] ?: false
        )
    }

    suspend fun saveConfig(botToken: String, chatId: String) {
        context.dataStore.edit { preferences ->
            preferences[BOT_TOKEN_KEY] = botToken
            preferences[CHAT_ID_KEY] = chatId
        }
    }

    suspend fun setServiceEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SERVICE_ENABLED_KEY] = enabled
        }
    }

    suspend fun getConfig(): AppConfig {
        val preferences = context.dataStore.data.map { it }.first()
        return AppConfig(
            botToken = preferences[BOT_TOKEN_KEY] ?: "",
            chatId = preferences[CHAT_ID_KEY] ?: "",
            serviceEnabled = preferences[SERVICE_ENABLED_KEY] ?: false
        )
    }

    suspend fun isServiceEnabled(): Boolean {
        val preferences = context.dataStore.data.map { it }.first()
        return preferences[SERVICE_ENABLED_KEY] ?: false
    }
}
