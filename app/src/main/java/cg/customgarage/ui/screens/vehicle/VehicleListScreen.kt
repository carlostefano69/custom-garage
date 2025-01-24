package cg.customgarage.ui.screens.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cg.customgarage.R
import cg.customgarage.data.models.Vehicle
import cg.customgarage.ui.theme.DeepBlue
import cg.customgarage.ui.theme.NeonBlue
import cg.customgarage.ui.viewmodels.VehicleViewModel

@Composable
fun VehicleListScreen(
    onVehicleClick: (String) -> Unit,
    onAddVehicleClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VehicleViewModel = viewModel()
) {
    val vehicles by viewModel.vehicles.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlue)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.vehicles_title),
                style = MaterialTheme.typography.headlineMedium,
                color = NeonBlue,
                fontWeight = FontWeight.Bold
            )
            
            FloatingActionButton(
                onClick = onAddVehicleClick,
                containerColor = NeonBlue
            ) {
                Icon(Icons.Default.Add, contentDescription = "Aggiungi Veicolo")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(vehicles) { vehicle ->
                VehicleCard(
                    vehicle = vehicle,
                    onClick = { onVehicleClick(vehicle.id) }
                )
            }
        }
    }
}

@Composable
private fun VehicleCard(
    vehicle: Vehicle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = vehicle.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${vehicle.make} ${vehicle.model} (${vehicle.year})",
                style = MaterialTheme.typography.bodyMedium
            )
            
            // Mostra il numero di manutenzioni programmate in arrivo
            vehicle.scheduledMaintenances.filter { 
                it.nextDueDate?.let { date -> date > System.currentTimeMillis() } ?: false 
            }.let { upcoming ->
                if (upcoming.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.upcoming_maintenance, upcoming.size),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
} 