package com.tonyakitori.apps.messageforwarder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tonyakitori.apps.messageforwarder.R
import com.tonyakitori.apps.messageforwarder.utils.BatteryOptimizationHelper

@Composable
fun BatteryOptimizationDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val manufacturerInfo = BatteryOptimizationHelper.getManufacturerInfo(context)
    val isIgnoringBattery = BatteryOptimizationHelper.isIgnoringBatteryOptimizations(context)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.battery_dialog_title),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isIgnoringBattery) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.errorContainer
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = if (isIgnoringBattery) {
                                stringResource(R.string.battery_status_disabled)
                            } else {
                                stringResource(R.string.battery_status_enabled)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isIgnoringBattery) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer
                            }
                        )
                    }
                }

                if (manufacturerInfo != null) {
                    Text(
                        text = stringResource(R.string.battery_device_detected, manufacturerInfo.name),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = manufacturerInfo.instructions,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = stringResource(R.string.battery_generic_instructions_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = BatteryOptimizationHelper.getGenericInstructions(context),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                HorizontalDivider()

                Text(
                    text = stringResource(R.string.battery_footer_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (manufacturerInfo != null) {
                    Button(
                        onClick = {
                            BatteryOptimizationHelper.openManufacturerSettings(context)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.button_open_autostart))
                    }
                }

                if (!isIgnoringBattery) {
                    OutlinedButton(
                        onClick = {
                            BatteryOptimizationHelper.openBatteryOptimizationSettings(context)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.button_disable_optimization))
                    }
                }

                TextButton(
                    onClick = {
                        BatteryOptimizationHelper.openAppSettings(context)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.button_open_app_settings))
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.button_close))
                }
            }
        },
        dismissButton = null
    )
}
