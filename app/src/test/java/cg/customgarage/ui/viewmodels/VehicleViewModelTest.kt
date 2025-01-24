package cg.customgarage.ui.viewmodels

import app.cash.turbine.test
import cg.customgarage.data.models.Vehicle
import cg.customgarage.data.repositories.VehicleRepository
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
class VehicleViewModelTest {
    private lateinit var repository: VehicleRepository
    private lateinit var viewModel: VehicleViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = VehicleViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `vehicles state is updated when repository emits new data`() = runTest {
        // Given
        val testVehicle = Vehicle(
            id = "1",
            name = "Test Vehicle",
            make = "Test Make",
            model = "Test Model",
            year = 2023
        )
        coEvery { repository.getAllVehicles() } returns flowOf(listOf(testVehicle))

        // When & Then
        viewModel.vehicles.test {
            val vehicles = awaitItem()
            assertEquals(1, vehicles.size)
            assertEquals("Test Vehicle", vehicles[0].name)
        }
    }

    @Test
    fun `addVehicle calls repository with correct data`() = runTest {
        // Given
        coEvery { repository.addVehicle(any()) } returns Unit

        // When
        viewModel.addVehicle(
            name = "Test Vehicle",
            make = "Test Make",
            model = "Test Model",
            year = 2023
        )

        // Then
        coVerify { repository.addVehicle(match { 
            it.name == "Test Vehicle" &&
            it.make == "Test Make" &&
            it.model == "Test Model" &&
            it.year == 2023
        })}
    }
} 