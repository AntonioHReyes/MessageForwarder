package com.tonyakitori.apps.messageforwarder.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.tonyakitori.apps.messageforwarder.data.local.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Boot completed, checking if service should be restarted")

            val preferencesManager = PreferencesManager(context)

            // Use to async operations
            val pendingResult = goAsync()
            
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val isServiceEnabled = preferencesManager.isServiceEnabled()
                    val config = preferencesManager.getConfig()

                    if (isServiceEnabled && config.isConfigured()) {
                        Log.d(TAG, "Restarting SMS forwarder service")
                        
                        val serviceIntent = Intent(context, SmsForwarderService::class.java).apply {
                            action = SmsForwarderService.ACTION_START_SERVICE
                        }

                        context.startForegroundService(serviceIntent)
                    } else {
                        Log.d(TAG, "Service not enabled or not configured, skipping restart")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error checking service status", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
