package com.tonyakitori.apps.messageforwarder.ui.home

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tonyakitori.apps.messageforwarder.data.local.PreferencesManager
import com.tonyakitori.apps.messageforwarder.data.models.AppConfig
import com.tonyakitori.apps.messageforwarder.service.SmsForwarderService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isServiceActive: Boolean = false,
    val isConfigured: Boolean = false,
    val lastMessageForwarded: String = ""
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeConfig()
    }

    private fun observeConfig() {
        viewModelScope.launch {
            preferencesManager.configFlow.collect { config ->
                _uiState.value = _uiState.value.copy(
                    isServiceActive = config.serviceEnabled,
                    isConfigured = config.isConfigured()
                )
            }
        }
    }

    fun startService() {
        if (!_uiState.value.isConfigured) {
            return
        }

        val intent = Intent(getApplication(), SmsForwarderService::class.java).apply {
            action = SmsForwarderService.ACTION_START_SERVICE
        }

        getApplication<Application>().startForegroundService(intent)
    }

    fun stopService() {
        val intent = Intent(getApplication(), SmsForwarderService::class.java).apply {
            action = SmsForwarderService.ACTION_STOP_SERVICE
        }
        getApplication<Application>().startService(intent)
    }

    fun toggleService() {
        if (_uiState.value.isServiceActive) {
            stopService()
        } else {
            startService()
        }
    }
}
