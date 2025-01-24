package cg.customgarage.data.repositories

import cg.customgarage.data.local.dao.SettingsDao
import cg.customgarage.data.local.entities.SettingsEntity
import cg.customgarage.data.models.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(
    private val settingsDao: SettingsDao
) {
    fun getSettings(): Flow<AppSettings> {
        return settingsDao.getSettings().map { entity ->
            entity?.toModel() ?: AppSettings()
        }
    }

    suspend fun updateSettings(settings: AppSettings) {
        settingsDao.updateSettings(settings.toEntity())
    }

    private fun SettingsEntity.toModel() = AppSettings(
        darkMode = darkMode,
        maintenanceNotifications = maintenanceNotifications,
        projectNotifications = projectNotifications,
        language = language
    )

    private fun AppSettings.toEntity() = SettingsEntity(
        darkMode = darkMode,
        maintenanceNotifications = maintenanceNotifications,
        projectNotifications = projectNotifications,
        language = language
    )
} 