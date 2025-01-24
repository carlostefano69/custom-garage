package cg.customgarage.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cg.customgarage.R
import cg.customgarage.data.models.ModificationStatus
import cg.customgarage.ui.theme.DeepBlue
import cg.customgarage.ui.theme.NeonBlue
import cg.customgarage.ui.theme.NeonGreen
import cg.customgarage.ui.viewmodels.ProjectViewModel

@Composable
fun ProjectDetailScreen(
    projectId: String,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProjectViewModel = viewModel()
) {
    val projectDetails by viewModel.getProjectDetails(projectId)
        .collectAsState(initial = null)

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
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = NeonBlue
                )
            }
            
            IconButton(onClick = onEditClick) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_project),
                    tint = NeonBlue
                )
            }
        }

        projectDetails?.let { details ->
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = details.project.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = NeonBlue,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Text(
                        text = details.project.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                item {
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.project_cost, details.totalCost),
                                style = MaterialTheme.typography.titleMedium,
                                color = NeonGreen
                            )
                            Text(
                                text = stringResource(R.string.project_status_value, details.project.status.name),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Sezione modifiche
                item {
                    Text(
                        text = stringResource(R.string.modifications_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = NeonBlue
                    )
                }

                items(details.project.modifications) { modification ->
                    ModificationCard(
                        modification = modification,
                        onStatusChange = { newStatus ->
                            viewModel.updateModificationStatus(
                                projectId = projectId,
                                modificationId = modification.id,
                                status = newStatus
                            )
                        }
                    )
                }

                // Sezione galleria
                if (details.inspirationImages.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.inspiration_images),
                            style = MaterialTheme.typography.titleLarge,
                            color = NeonBlue
                        )
                    }

                    // TODO: Implementare griglia immagini
                }
            }
        }
    }
} 