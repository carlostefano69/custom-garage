package cg.customgarage.data.repositories

import cg.customgarage.data.local.dao.VehicleDao
import cg.customgarage.data.local.dao.MaintenanceDao
import cg.customgarage.data.local.entities.*
import cg.customgarage.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class VehicleRepository(
    private val vehicleDao: VehicleDao,
    private val maintenanceDao: MaintenanceDao
) {
    fun getAllVehicles(): Flow<List<Vehicle>> {
        return vehicleDao.getAllVehicles().map { entities ->
            entities.map { it.toModel() }
        }
    }

    suspend fun getVehicleWithMaintenance(id: String): Flow<VehicleWithMaintenance?> {
        val vehicle = vehicleDao.getVehicleById(id)?.toModel() ?: return kotlinx.coroutines.flow.flowOf(null)
        
        return kotlinx.coroutines.flow.combine(
            maintenanceDao.getScheduledMaintenances(id),
            maintenanceDao.getMaintenanceRecords(id)
        ) { scheduled, records ->
            VehicleWithMaintenance(
                vehicle = vehicle,
                upcomingMaintenances = scheduled.map { it.toModel() },
                maintenanceRecords = records.map { it.toModel() }
            )
        }
    }

    suspend fun addVehicle(vehicle: Vehicle) {
        vehicleDao.insertVehicle(vehicle.toEntity())
    }

    suspend fun scheduleNewMaintenance(maintenance: ScheduledMaintenance, vehicleId: String) {
        maintenanceDao.insertScheduledMaintenance(maintenance.toEntity(vehicleId))
    }

    suspend fun addMaintenanceRecord(record: MaintenanceRecord, vehicleId: String) {
        maintenanceDao.insertMaintenanceRecord(record.toEntity(vehicleId))
    }

    private fun VehicleEntity.toModel() = Vehicle(
        id = id,
        name = name,
        make = make,
        model = model,
        year = year,
        imageUrl = imageUrl
    )

    private fun Vehicle.toEntity() = VehicleEntity(
        id = id,
        name = name,
        make = make,
        model = model,
        year = year,
        imageUrl = imageUrl
    )

    private fun ScheduledMaintenanceEntity.toModel() = ScheduledMaintenance(
        id = id,
        type = type,
        description = description,
        intervalMonths = intervalMonths,
        intervalMileage = intervalMileage,
        estimatedCost = estimatedCost,
        nextDueDate = nextDueDate,
        nextDueMileage = nextDueMileage
    )

    private fun ScheduledMaintenance.toEntity(vehicleId: String) = ScheduledMaintenanceEntity(
        id = id,
        vehicleId = vehicleId,
        type = type,
        description = description,
        intervalMonths = intervalMonths,
        intervalMileage = intervalMileage,
        estimatedCost = estimatedCost,
        nextDueDate = nextDueDate,
        nextDueMileage = nextDueMileage
    )

    private fun MaintenanceRecordEntity.toModel() = MaintenanceRecord(
        id = id,
        date = date,
        type = type,
        description = description,
        mileage = mileage,
        cost = cost,
        notes = notes
    )

    private fun MaintenanceRecord.toEntity(vehicleId: String) = MaintenanceRecordEntity(
        id = id,
        vehicleId = vehicleId,
        date = date,
        type = type,
        description = description,
        mileage = mileage,
        cost = cost,
        notes = notes
    )
} 