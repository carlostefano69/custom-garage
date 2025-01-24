package cg.customgarage.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cg.customgarage.R
import cg.customgarage.ui.theme.DeepBlue
import cg.customgarage.ui.theme.NeonBlue

@Composable
fun ProjectsScreen(
    onProjectClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlue)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.projects_title),
            style = MaterialTheme.typography.headlineMedium,
            color = NeonBlue,
            fontWeight = FontWeight.Bold
        )

        // TODO: Implementare filtri e lista progetti
    }
} 