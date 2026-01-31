package com.tonyakitori.apps.messageforwarder.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings

object BatteryOptimizationHelper {

    data class ManufacturerInfo(
        val name: String,
        val instructions: String,
        val intents: List<Intent>
    )

    private val manufacturerSettings: Map<String, ManufacturerInfo> by lazy {
        mapOf(
            "xiaomi" to ManufacturerInfo(
                name = "Xiaomi/Redmi/POCO",
                instructions = """
                    1. Ve a Ajustes → Aplicaciones → Gestionar aplicaciones
                    2. Busca "Message Forwarder"
                    3. Activa "Autostart" (Inicio automático)
                    4. En "Ahorro de batería" selecciona "Sin restricciones"
                """.trimIndent(),
                intents = listOf(
                    Intent().apply {
                        component = ComponentName(
                            "com.miui.securitycenter",
                            "com.miui.permcenter.autostart.AutoStartManagementActivity"
                        )
                    },
                    Intent().apply {
                        component = ComponentName(
                            "com.miui.securitycenter",
                            "com.miui.powercenter.PowerSettings"
                        )
                    }
                )
            ),
            "oppo" to ManufacturerInfo(
                name = "OPPO/Realme",
                instructions = """
                    1. Ve a Ajustes → Gestión de aplicaciones
                    2. Busca "Message Forwarder"
                    3. Activa "Permitir inicio automático"
                    4. Desactiva "Optimización de batería"
                """.trimIndent(),
                intents = listOf(
                    Intent().apply {
                        component = ComponentName(
                            "com.coloros.safecenter",
                            "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                        )
                    },
                    Intent().apply {
                        component = ComponentName(
                            "com.coloros.safecenter",
                            "com.coloros.safecenter.startupapp.StartupAppListActivity"
                        )
                    }
                )
            ),
            "vivo" to ManufacturerInfo(
                name = "Vivo",
                instructions = """
                    1. Ve a Ajustes → Batería → Alta actividad en segundo plano
                    2. Activa "Message Forwarder"
                    3. En Gestor de permisos, activa "Inicio automático"
                """.trimIndent(),
                intents = listOf(
                    Intent().apply {
                        component = ComponentName(
                            "com.vivo.permissionmanager",
                            "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                        )
                    }
                )
            ),
            "huawei" to ManufacturerInfo(
                name = "Huawei/Honor",
                instructions = """
                    1. Ve a Ajustes → Aplicaciones → Aplicaciones
                    2. Busca "Message Forwarder" → Batería
                    3. Desactiva "Gestión automática"
                    4. Activa todas las opciones manualmente
                """.trimIndent(),
                intents = listOf(
                    Intent().apply {
                        component = ComponentName(
                            "com.huawei.systemmanager",
                            "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
                        )
                    },
                    Intent().apply {
                        component = ComponentName(
                            "com.huawei.systemmanager",
                            "com.huawei.systemmanager.optimize.process.ProtectActivity"
                        )
                    }
                )
            ),
            "samsung" to ManufacturerInfo(
                name = "Samsung",
                instructions = """
                    1. Ve a Ajustes → Cuidado del dispositivo → Batería
                    2. Toca "Límites de uso en segundo plano"
                    3. Añade "Message Forwarder" a "Apps que no se suspenden"
                    4. Desactiva "Poner apps no utilizadas en suspensión"
                """.trimIndent(),
                intents = listOf(
                    Intent().apply {
                        component = ComponentName(
                            "com.samsung.android.lool",
                            "com.samsung.android.sm.battery.ui.BatteryActivity"
                        )
                    }
                )
            ),
            "oneplus" to ManufacturerInfo(
                name = "OnePlus",
                instructions = """
                    1. Ve a Ajustes → Batería → Optimización de batería
                    2. Busca "Message Forwarder"
                    3. Selecciona "No optimizar"
                """.trimIndent(),
                intents = listOf(
                    Intent().apply {
                        component = ComponentName(
                            "com.oneplus.security",
                            "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity"
                        )
                    }
                )
            ),
            "asus" to ManufacturerInfo(
                name = "ASUS",
                instructions = """
                    1. Ve a Ajustes → Administración de energía
                    2. Toca "Auto-start Manager"
                    3. Activa "Message Forwarder"
                """.trimIndent(),
                intents = listOf(
                    Intent().apply {
                        component = ComponentName(
                            "com.asus.mobilemanager",
                            "com.asus.mobilemanager.autostart.AutoStartActivity"
                        )
                    }
                )
            )
        )
    }

    fun getManufacturer(): String = Build.MANUFACTURER.lowercase()

    fun getManufacturerInfo(): ManufacturerInfo? {
        val manufacturer = getManufacturer()
        return manufacturerSettings.entries.firstOrNull { (key, _) ->
            manufacturer.contains(key)
        }?.value
    }

    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    fun openBatteryOptimizationSettings(context: Context): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun openManufacturerSettings(context: Context): Boolean {
        val info = getManufacturerInfo() ?: return false

        for (intent in info.intents) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return true
            } catch (e: Exception) {
            }
        }
        return false
    }

    fun openAppSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    fun getGenericInstructions(): String = """
        1. Ve a Ajustes → Aplicaciones → Message Forwarder
        2. Toca en "Batería" o "Uso de batería"
        3. Selecciona "Sin restricciones" o "No optimizar"
        4. Si existe opción de "Autostart" o "Inicio automático", actívala
    """.trimIndent()
}
