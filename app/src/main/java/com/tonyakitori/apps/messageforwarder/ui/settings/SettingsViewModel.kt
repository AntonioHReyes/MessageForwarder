package com.tonyakitori.apps.messageforwarder.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tonyakitori.apps.messageforwarder.R
import com.tonyakitori.apps.messageforwarder.data.local.PreferencesManager
import com.tonyakitori.apps.messageforwarder.data.remote.TelegramClient
import com.tonyakitori.apps.messageforwarder.data.repository.SmsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val botToken: String = "",
    val chatId: String = "",
    val isTestingConnection: Boolean = false,
    val testResult: TestResult? = null
)

sealed class TestResult {
    object Success : TestResult()
    data class Error(val message: String) : TestResult()
}

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val telegramClient = TelegramClient()
    private val smsRepository = SmsRepository(telegramClient)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadConfig()
    }

    private fun loadConfig() {
        viewModelScope.launch {
            preferencesManager.configFlow.collect { config ->
                _uiState.value = _uiState.value.copy(
                    botToken = config.botToken,
                    chatId = config.chatId
                )
            }
        }
    }

    fun updateBotToken(token: String) {
        _uiState.value = _uiState.value.copy(botToken = token)
    }

    fun updateChatId(chatId: String) {
        _uiState.value = _uiState.value.copy(chatId = chatId)
    }

    fun saveConfig() {
        viewModelScope.launch {
            preferencesManager.saveConfig(
                botToken = _uiState.value.botToken,
                chatId = _uiState.value.chatId
            )
        }
    }

    fun testConnection() {
        val currentState = _uiState.value
        
        if (currentState.botToken.isBlank() || currentState.chatId.isBlank()) {
            _uiState.value = currentState.copy(
                testResult = TestResult.Error(getApplication<Application>().getString(R.string.error_fill_all_fields))
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(
                isTestingConnection = true,
                testResult = null
            )

            val result = smsRepository.testConnection(
                botToken = currentState.botToken,
                chatId = currentState.chatId
            )

            _uiState.value = _uiState.value.copy(
                isTestingConnection = false,
                testResult = if (result.isSuccess) {
                    TestResult.Success
                } else {
                    TestResult.Error(
                        result.exceptionOrNull()?.message
                            ?: getApplication<Application>().getString(R.string.error_unknown)
                    )
                }
            )
        }
    }

    fun clearTestResult() {
        _uiState.value = _uiState.value.copy(testResult = null)
    }

    override fun onCleared() {
        super.onCleared()
        telegramClient.close()
    }
}
