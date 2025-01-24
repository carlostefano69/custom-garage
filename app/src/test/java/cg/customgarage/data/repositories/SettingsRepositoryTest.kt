package cg.customgarage.data.repositories

import app.cash.turbine.test
import cg.customgarage.data.local.dao.SettingsDao
import cg.customgarage.data.models.AppSettings
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SettingsRepositoryTest {
    private lateinit var settingsDao: SettingsDao
    private lateinit var repository: SettingsRepository

    @Before
    fun setup() {
        settingsDao = mockk()
        repository = SettingsRepository(settingsDao)
    }

    @Test
    fun `getSettings returns default settings when no settings stored`() = runTest {
        // Given
        coEvery { settingsDao.getSettings() } returns flowOf(null)

        // When & Then
        repository.getSettings().test {
            val settings = awaitItem()
            assertEquals(true, settings.darkMode) // default value
            assertEquals("it", settings.language) // default value
            awaitComplete()
        }
    }

    @Test
    fun `updateSettings correctly stores settings`() = runTest {
        // Given
        val settings = AppSettings(
            darkMode = false,
            maintenanceNotifications = false,
            projectNotifications = true,
            language = "en"
        )
        coEvery { settingsDao.updateSettings(any()) } returns Unit

        // When
        repository.updateSettings(settings)

        // Then
        coVerify { settingsDao.updateSettings(match { 
            it.darkMode == false && 
            it.language == "en" && 
            it.maintenanceNotifications == false &&
            it.projectNotifications == true
        })}
    }
} 