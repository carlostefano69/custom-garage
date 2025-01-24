package cg.customgarage.ui.screens.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cg.customgarage.data.models.MaintenanceRecord
import cg.customgarage.data.models.MaintenanceType
import cg.customgarage.data.models.ScheduledMaintenance
import cg.customgarage.ui.theme.DeepBlue
import cg.customgarage.ui.theme.NeonBlue
import cg.customgarage.ui.theme.NeonGreen
import cg.customgarage.ui.viewmodels.VehicleViewModel
import cg.customgarage.ui.viewmodels.VehicleWithMaintenance
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.res.stringResource
import cg.customgarage.R

@Composable
fun VehicleMaintenanceScreen(
    vehicleId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VehicleViewModel = viewModel()
) {
    val vehicleData by viewModel.getVehicleWithMaintenance(vehicleId)
        .collectAsState(initial = null)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlue)
            .padding(16.dp)
    ) {
        vehicleData?.let { data ->
            Text(
                text = data.vehicle.name,
                style = MaterialTheme.typography.headlineMedium,
                color = NeonBlue,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sezione manutenzioni programmate
                item {
                    Text(
                        text = stringResource(R.string.scheduled_maintenance),
                        style = MaterialTheme.typography.titleLarge,
                        color = NeonGreen
                    )
                }

                items(data.upcomingMaintenances) { maintenance ->
                    ScheduledMaintenanceCard(
                        maintenance = maintenance,
                        onComplete = { mileage, cost, notes ->
                            viewModel.completeScheduledMaintenance(
                                vehicleId = vehicleId,
                                maintenanceId = maintenance.id,
                                mileage = mileage,
                                cost = cost,
                                notes = notes
                            )
                        }
                    )
                }

                // Sezione storico manutenzioni
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.maintenance_history),
                        style = MaterialTheme.typography.titleLarge,
                        color = NeonGreen
                    )
                }

                items(data.maintenanceRecords.sortedByDescending { it.date }) { record ->
                    MaintenanceRecordCard(record = record)
                }
            }
        }
    }
}

@Composable
private fun ScheduledMaintenanceCard(
    maintenance: ScheduledMaintenance,
    onComplete: (mileage: Int, cost: Double, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCompleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = maintenance.description,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Tipo: ${maintenance.type.name}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            maintenance.nextDueDate?.let { date ->
                Text(
                    text = "Scadenza: ${SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN).format(Date(date))}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = { showCompleteDialog = true },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Completa")
            }
        }
    }

    if (showCompleteDialog) {
        CompleteMaintenanceDialog(
            onDismiss = { showCompleteDialog = false },
            onComplete = onComplete
        )
    }
}

@Composable
private fun MaintenanceRecordCard(
    record: MaintenanceRecord,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = record.description,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = stringResource(R.string.maintenance_date, 
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(record.date))),
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = stringResource(R.string.maintenance_mileage_value, record.mileage),
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = stringResource(R.string.maintenance_cost_value, record.cost),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun CompleteMaintenanceDialog(
    onDismiss: () -> Unit,
    onComplete: (mileage: Int, cost: Double, notes: String) -> Unit
) {
    var mileage by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.maintenance_complete)) },
        text = {
            Column {
                OutlinedTextField(
                    value = mileage,
                    onValueChange = { mileage = it },
                    label = { Text(stringResource(R.string.maintenance_mileage)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = cost,
                    onValueChange = { cost = it },
                    label = { Text(stringResource(R.string.maintenance_cost)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(stringResource(R.string.maintenance_notes)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onComplete(
                        mileage.toIntOrNull() ?: 0,
                        cost.toDoubleOrNull() ?: 0.0,
                        notes
                    )
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
} 