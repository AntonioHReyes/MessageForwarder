package com.tonyakitori.apps.messageforwarder.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.tonyakitori.apps.messageforwarder.MainActivity
import com.tonyakitori.apps.messageforwarder.R
import com.tonyakitori.apps.messageforwarder.data.local.PreferencesManager
import com.tonyakitori.apps.messageforwarder.data.models.SmsMessage
import com.tonyakitori.apps.messageforwarder.data.remote.TelegramClient
import com.tonyakitori.apps.messageforwarder.data.repository.SmsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SmsForwarderService : Service() {

    companion object {
        const val TAG = "SmsForwarderService"
        const val CHANNEL_ID = "sms_forwarder_channel"
        const val NOTIFICATION_ID = 1

        const val ACTION_START_SERVICE = "com.tonyakitori.apps.messageforwarder.START_SERVICE"
        const val ACTION_STOP_SERVICE = "com.tonyakitori.apps.messageforwarder.STOP_SERVICE"
        const val ACTION_FORWARD_SMS = "com.tonyakitori.apps.messageforwarder.FORWARD_SMS"
    }

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var smsRepository: SmsRepository
    private lateinit var notificationManager: NotificationManager

    private var isServiceRunning = false
    private var lastSmsForwarded: String = ""

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
        
        preferencesManager = PreferencesManager(applicationContext)
        val telegramClient = TelegramClient()
        smsRepository = SmsRepository(telegramClient)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started with action: ${intent?.action}")

        if (!isServiceRunning) {
            startForeground(NOTIFICATION_ID, createNotification("Servicio activo", "Esperando mensajes SMS..."))
            isServiceRunning = true
        }

        when (intent?.action) {
            ACTION_START_SERVICE -> {
                serviceScope.launch {
                    preferencesManager.setServiceEnabled(true)
                }
            }
            ACTION_STOP_SERVICE -> {
                serviceScope.launch {
                    preferencesManager.setServiceEnabled(false)
                }
                isServiceRunning = false
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            ACTION_FORWARD_SMS -> {
                handleForwardSms(intent)
            }
        }

        return START_STICKY
    }

    private fun handleForwardSms(intent: Intent) {
        val sender = intent.getStringExtra(SmsReceiver.EXTRA_SMS_SENDER) ?: return
        val message = intent.getStringExtra(SmsReceiver.EXTRA_SMS_MESSAGE) ?: return
        val timestamp = intent.getLongExtra(SmsReceiver.EXTRA_SMS_TIMESTAMP, System.currentTimeMillis())

        val smsMessage = SmsMessage(
            sender = sender,
            message = message,
            timestamp = timestamp
        )

        serviceScope.launch {
            try {
                val config = preferencesManager.getConfig()
                
                if (!config.isConfigured()) {
                    Log.w(TAG, "Service not configured, skipping SMS forward")
                    updateNotification("Error", "Bot no configurado")
                    return@launch
                }

                updateNotification("Reenviando SMS", "De: $sender")

                val result = smsRepository.forwardSmsToTelegram(smsMessage, config.botToken, config.chatId)

                if (result.isSuccess) {
                    lastSmsForwarded = "Último: ${sender.take(10)}..."
                    updateNotification("SMS reenviado", lastSmsForwarded)
                    Log.d(TAG, "SMS forwarded successfully")
                } else {
                    updateNotification("Error al reenviar", result.exceptionOrNull()?.message ?: "Error desconocido")
                    Log.e(TAG, "Failed to forward SMS", result.exceptionOrNull())
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling SMS", e)
                updateNotification("Error", e.message ?: "Error desconocido")
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "SMS Forwarder Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Notificación del servicio de reenvío de SMS"
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(title: String, content: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(title: String, content: String) {
        val notification = createNotification(title, content)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        serviceScope.cancel()
        Log.d(TAG, "Service destroyed")
    }
}
