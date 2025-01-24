package cg.customgarage.ui.viewmodels

import app.cash.turbine.test
import cg.customgarage.data.models.AppSettings
import cg.customgarage.data.repositories.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private lateinit var repository: SettingsRepository
    private lateinit var viewModel: SettingsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = SettingsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `settings state is updated when repository emits new data`() = runTest {
        // Given
        val settings = AppSettings(
            darkMode = true,
            maintenanceNotifications = true,
            projectNotifications = false,
            language = "it"
        )
        coEvery { repository.getSettings() } returns flowOf(settings)

        // When & Then
        viewModel.settings.test {
            val result = awaitItem()
            assertEquals(true, result.darkMode)
            assertEquals("it", result.language)
            assertEquals(true, result.maintenanceNotifications)
            assertEquals(false, result.projectNotifications)
        }
    }

    @Test
    fun `updateSettings calls repository with correct data`() = runTest {
        // Given
        coEvery { repository.updateSettings(any()) } returns Unit

        // When
        viewModel.updateSettings(
            darkMode = false,
            maintenanceNotifications = true,
            projectNotifications = true,
            language = "en"
        )

        // Then
        coVerify { repository.updateSettings(match { 
            it.darkMode == false &&
            it.maintenanceNotifications == true &&
            it.projectNotifications == true &&
            it.language == "en"
        })}
    }

    @Test
    fun `updateSettings only updates specified fields`() = runTest {
        // Given
        val initialSettings = AppSettings(
            darkMode = true,
            maintenanceNotifications = true,
            projectNotifications = true,
            language = "it"
        )
        coEvery { repository.getSettings() } returns flowOf(initialSettings)
        coEvery { repository.updateSettings(any()) } returns Unit

        // When
        viewModel.updateSettings(darkMode = false)

        // Then
        coVerify { repository.updateSettings(match { 
            it.darkMode == false &&
            it.maintenanceNotifications == true &&
            it.projectNotifications == true &&
            it.language == "it"
        })}
    }
} 