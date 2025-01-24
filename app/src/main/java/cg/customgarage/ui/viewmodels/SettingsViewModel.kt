package cg.customgarage.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cg.customgarage.data.models.AppSettings
import cg.customgarage.data.repositories.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = repository.getSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    fun updateSettings(
        darkMode: Boolean? = null,
        maintenanceNotifications: Boolean? = null,
        projectNotifications: Boolean? = null,
        language: String? = null
    ) {
        val currentSettings = settings.value
        val updatedSettings = currentSettings.copy(
            darkMode = darkMode ?: currentSettings.darkMode,
            maintenanceNotifications = maintenanceNotifications ?: currentSettings.maintenanceNotifications,
            projectNotifications = projectNotifications ?: currentSettings.projectNotifications,
            language = language ?: currentSettings.language
        )
        viewModelScope.launch {
            repository.updateSettings(updatedSettings)
        }
    }
} 