package cg.customgarage.data.repositories

import app.cash.turbine.test
import cg.customgarage.data.local.dao.MaintenanceDao
import cg.customgarage.data.local.dao.VehicleDao
import cg.customgarage.data.local.entities.VehicleEntity
import cg.customgarage.data.models.Vehicle
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class VehicleRepositoryTest {
    private lateinit var vehicleDao: VehicleDao
    private lateinit var maintenanceDao: MaintenanceDao
    private lateinit var repository: VehicleRepository

    @Before
    fun setup() {
        vehicleDao = mockk()
        maintenanceDao = mockk()
        repository = VehicleRepository(vehicleDao, maintenanceDao)
    }

    @Test
    fun `getAllVehicles returns mapped vehicles`() = runTest {
        // Given
        val vehicleEntity = VehicleEntity(
            id = "1",
            name = "Test Vehicle",
            make = "Test Make",
            model = "Test Model",
            year = 2023,
            imageUrl = null
        )
        coEvery { vehicleDao.getAllVehicles() } returns flowOf(listOf(vehicleEntity))

        // When & Then
        repository.getAllVehicles().test {
            val vehicles = awaitItem()
            assertEquals(1, vehicles.size)
            assertEquals("Test Vehicle", vehicles[0].name)
            awaitComplete()
        }
    }

    @Test
    fun `addVehicle correctly stores vehicle`() = runTest {
        // Given
        val vehicle = Vehicle(
            id = "1",
            name = "Test Vehicle",
            make = "Test Make",
            model = "Test Model",
            year = 2023
        )
        coEvery { vehicleDao.insertVehicle(any()) } returns Unit

        // When
        repository.addVehicle(vehicle)

        // Then
        coEvery { vehicleDao.getVehicleById("1") } returns vehicle.toEntity()
        val storedVehicle = vehicleDao.getVehicleById("1")
        assertEquals(vehicle.name, storedVehicle?.name)
    }
} 