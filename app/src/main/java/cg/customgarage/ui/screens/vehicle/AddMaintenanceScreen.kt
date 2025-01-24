package cg.customgarage.ui.screens.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cg.customgarage.data.models.MaintenanceType
import cg.customgarage.ui.theme.DeepBlue
import cg.customgarage.ui.theme.NeonBlue
import cg.customgarage.ui.viewmodels.VehicleViewModel
import androidx.compose.ui.res.stringResource
import cg.customgarage.R

@Composable
fun AddMaintenanceScreen(
    vehicleId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VehicleViewModel = viewModel()
) {
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(MaintenanceType.ORDINARY) }
    var intervalMonths by remember { mutableStateOf("") }
    var intervalMileage by remember { mutableStateOf("") }
    var estimatedCost by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlue)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = NeonBlue
                )
            }
            
            Text(
                text = stringResource(R.string.add_maintenance),
                style = MaterialTheme.typography.headlineMedium,
                color = NeonBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.maintenance_type),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        MaintenanceTypeSelector(
            selectedType = selectedType,
            onTypeSelected = { selectedType = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(R.string.maintenance_description)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonBlue,
                focusedLabelColor = NeonBlue
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = intervalMonths,
            onValueChange = { intervalMonths = it },
            label = { Text(stringResource(R.string.maintenance_interval_months)) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonBlue,
                focusedLabelColor = NeonBlue
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = intervalMileage,
            onValueChange = { intervalMileage = it },
            label = { Text(stringResource(R.string.maintenance_interval_mileage)) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonBlue,
                focusedLabelColor = NeonBlue
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = estimatedCost,
            onValueChange = { estimatedCost = it },
            label = { Text(stringResource(R.string.maintenance_estimated_cost)) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonBlue,
                focusedLabelColor = NeonBlue
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val months = intervalMonths.toIntOrNull() ?: 0
                val mileage = intervalMileage.toIntOrNull() ?: 0
                val cost = estimatedCost.toDoubleOrNull()

                viewModel.scheduleNewMaintenance(
                    vehicleId = vehicleId,
                    type = selectedType,
                    description = description,
                    intervalMonths = months,
                    intervalMileage = mileage,
                    estimatedCost = cost
                )
                onNavigateBack()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = description.isNotBlank() && 
                     intervalMonths.isNotBlank() && intervalMonths.toIntOrNull() != null &&
                     intervalMileage.isNotBlank() && intervalMileage.toIntOrNull() != null
        ) {
            Text(stringResource(R.string.confirm))
        }
    }
}

@Composable
private fun MaintenanceTypeSelector(
    selectedType: MaintenanceType,
    onTypeSelected: (MaintenanceType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        MaintenanceType.values().forEach { type ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                RadioButton(
                    selected = type == selectedType,
                    onClick = { onTypeSelected(type) }
                )
                Text(
                    text = when (type) {
                        MaintenanceType.ORDINARY -> stringResource(R.string.maintenance_type_ordinary)
                        MaintenanceType.EXTRAORDINARY -> stringResource(R.string.maintenance_type_extraordinary)
                        MaintenanceType.INSPECTION -> stringResource(R.string.maintenance_type_inspection)
                        MaintenanceType.MODIFICATION -> stringResource(R.string.maintenance_type_modification)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
} 