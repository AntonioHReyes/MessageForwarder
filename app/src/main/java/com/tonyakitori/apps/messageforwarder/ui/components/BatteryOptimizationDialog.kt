package com.tonyakitori.apps.messageforwarder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tonyakitori.apps.messageforwarder.utils.BatteryOptimizationHelper

@Composable
fun BatteryOptimizationDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val manufacturerInfo = BatteryOptimizationHelper.getManufacturerInfo()
    val isIgnoringBattery = BatteryOptimizationHelper.isIgnoringBatteryOptimizations(context)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Configurar permisos",
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
                                "Optimización de batería: Desactivada"
                            } else {
                                "Optimización de batería: Activada (puede causar problemas)"
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
                        text = "Dispositivo ${manufacturerInfo.name} detectado",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = manufacturerInfo.instructions,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = "Instrucciones generales",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = BatteryOptimizationHelper.getGenericInstructions(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                HorizontalDivider()

                Text(
                    text = "Para que la app funcione correctamente en segundo plano, necesitas desactivar la optimización de batería y activar el inicio automático.",
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
                        Text("Abrir Autostart")
                    }
                }

                if (!isIgnoringBattery) {
                    OutlinedButton(
                        onClick = {
                            BatteryOptimizationHelper.openBatteryOptimizationSettings(context)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Desactivar optimización")
                    }
                }

                TextButton(
                    onClick = {
                        BatteryOptimizationHelper.openAppSettings(context)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Abrir ajustes de la app")
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar")
                }
            }
        },
        dismissButton = null
    )
}
