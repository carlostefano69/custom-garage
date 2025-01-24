package cg.customgarage.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cg.customgarage.R
import cg.customgarage.data.models.Modification
import cg.customgarage.data.models.ModificationStatus
import java.text.NumberFormat
import java.util.*

@Composable
fun ModificationCard(
    modification: Modification,
    onStatusChange: (ModificationStatus) -> Unit,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = modification.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                StatusChip(
                    status = modification.status,
                    onClick = { onStatusChange(it) }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = modification.description,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = stringResource(
                    R.string.modification_cost,
                    NumberFormat.getCurrencyInstance(Locale.ITALY).format(modification.cost)
                ),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun StatusChip(
    status: ModificationStatus,
    onClick: (ModificationStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box {
        AssistChip(
            onClick = { expanded = true },
            label = { 
                Text(
                    text = when (status) {
                        ModificationStatus.PLANNED -> stringResource(R.string.status_planned)
                        ModificationStatus.IN_PROGRESS -> stringResource(R.string.status_in_progress)
                        ModificationStatus.COMPLETED -> stringResource(R.string.status_completed)
                        ModificationStatus.CANCELLED -> stringResource(R.string.status_cancelled)
                    }
                )
            },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = when (status) {
                    ModificationStatus.PLANNED -> MaterialTheme.colorScheme.primaryContainer
                    ModificationStatus.IN_PROGRESS -> MaterialTheme.colorScheme.secondaryContainer
                    ModificationStatus.COMPLETED -> MaterialTheme.colorScheme.tertiaryContainer
                    ModificationStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
                }
            )
        )
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ModificationStatus.values().forEach { newStatus ->
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = when (newStatus) {
                                ModificationStatus.PLANNED -> stringResource(R.string.status_planned)
                                ModificationStatus.IN_PROGRESS -> stringResource(R.string.status_in_progress)
                                ModificationStatus.COMPLETED -> stringResource(R.string.status_completed)
                                ModificationStatus.CANCELLED -> stringResource(R.string.status_cancelled)
                            }
                        )
                    },
                    onClick = {
                        onClick(newStatus)
                        expanded = false
                    }
                )
            }
        }
    }
} 