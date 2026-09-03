package com.tonyakitori.apps.messageforwarder.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import com.tonyakitori.apps.messageforwarder.R

object BatteryOptimizationHelper {

    data class ManufacturerInfo(
        val name: String,
        val instructions: String,
        val intents: List<Intent>
    )

    private fun manufacturerSettings(context: Context): Map<String, ManufacturerInfo> {
        return mapOf(
            "xiaomi" to ManufacturerInfo(
                name = context.getString(R.string.manufacturer_xiaomi_name),
                instructions = context.getString(R.string.manufacturer_xiaomi_instructions),
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
                name = context.getString(R.string.manufacturer_oppo_name),
                instructions = context.getString(R.string.manufacturer_oppo_instructions),
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
                name = context.getString(R.string.manufacturer_vivo_name),
                instructions = context.getString(R.string.manufacturer_vivo_instructions),
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
                name = context.getString(R.string.manufacturer_huawei_name),
                instructions = context.getString(R.string.manufacturer_huawei_instructions),
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
                name = context.getString(R.string.manufacturer_samsung_name),
                instructions = context.getString(R.string.manufacturer_samsung_instructions),
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
                name = context.getString(R.string.manufacturer_oneplus_name),
                instructions = context.getString(R.string.manufacturer_oneplus_instructions),
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
                name = context.getString(R.string.manufacturer_asus_name),
                instructions = context.getString(R.string.manufacturer_asus_instructions),
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

    fun getManufacturerInfo(context: Context): ManufacturerInfo? {
        val manufacturer = getManufacturer()
        return manufacturerSettings(context).entries.firstOrNull { (key, _) ->
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
        val info = getManufacturerInfo(context) ?: return false

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

    fun getGenericInstructions(context: Context): String =
        context.getString(R.string.battery_generic_instructions)
}
