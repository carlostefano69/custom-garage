package cg.customgarage.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlue)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = NeonBlue
                )
            }
            
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineMedium,
                color = NeonBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Impostazioni tema
        Text(
            text = stringResource(R.string.theme_settings),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        var darkMode by remember { mutableStateOf(true) }
        Switch(
            checked = darkMode,
            onCheckedChange = { darkMode = it },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Impostazioni notifiche
        Text(
            text = stringResource(R.string.notification_settings),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        var maintenanceNotifications by remember { mutableStateOf(true) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.maintenance_notifications))
            Switch(
                checked = maintenanceNotifications,
                onCheckedChange = { maintenanceNotifications = it }
            )
        }

        var projectNotifications by remember { mutableStateOf(true) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.project_notifications))
            Switch(
                checked = projectNotifications,
                onCheckedChange = { projectNotifications = it }
            )
        }
    }
} 