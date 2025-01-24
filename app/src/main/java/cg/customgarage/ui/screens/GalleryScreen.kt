package cg.customgarage.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cg.customgarage.R
import cg.customgarage.ui.theme.DeepBlue
import cg.customgarage.ui.theme.NeonBlue

@Composable
fun GalleryScreen(
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
                text = stringResource(R.string.gallery_title),
                style = MaterialTheme.typography.headlineMedium,
                color = NeonBlue,
                fontWeight = FontWeight.Bold
            )
            
            FloatingActionButton(
                onClick = { /* TODO: Implementare caricamento immagine */ },
                containerColor = NeonBlue
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_image)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filtri per categoria
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssistChip(
                onClick = { /* TODO */ },
                label = { Text(stringResource(R.string.gallery_filter_all)) }
            )
            AssistChip(
                onClick = { /* TODO */ },
                label = { Text(stringResource(R.string.gallery_filter_projects)) }
            )
            AssistChip(
                onClick = { /* TODO */ },
                label = { Text(stringResource(R.string.gallery_filter_maintenance)) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // TODO: Grid di immagini
        }
    }
} 