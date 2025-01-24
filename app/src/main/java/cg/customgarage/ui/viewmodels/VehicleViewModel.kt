package cg.customgarage.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cg.customgarage.data.models.*
import cg.customgarage.data.repositories.VehicleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class VehicleViewModel @Inject constructor(
    private val repository: VehicleRepository
) : ViewModel() {

    val vehicles: StateFlow<List<Vehicle>> = repository.getAllVehicles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getVehicleWithMaintenance(vehicleId: String): StateFlow<VehicleWithMaintenance?> {
        return repository.getVehicleWithMaintenance(vehicleId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    }

    fun addVehicle(
        name: String,
        make: String,
        model: String,
        year: Int
    ) {
        val vehicle = Vehicle(
            name = name,
            make = make,
            model = model,
            year = year
        )
        viewModelScope.launch {
            repository.addVehicle(vehicle)
        }
    }

    fun scheduleNewMaintenance(
        vehicleId: String,
        type: MaintenanceType,
        description: String,
        intervalMonths: Int,
        intervalMileage: Int,
        estimatedCost: Double?
    ) {
        val maintenance = ScheduledMaintenance(
            type = type,
            description = description,
            intervalMonths = intervalMonths,
            intervalMileage = intervalMileage,
            estimatedCost = estimatedCost,
            nextDueDate = Calendar.getInstance().apply {
                add(Calendar.MONTH, intervalMonths)
            }.timeInMillis
        )
        viewModelScope.launch {
            repository.scheduleNewMaintenance(maintenance, vehicleId)
        }
    }

    fun completeScheduledMaintenance(
        vehicleId: String,
        maintenanceId: String,
        mileage: Int,
        cost: Double,
        notes: String
    ) {
        val record = MaintenanceRecord(
            date = System.currentTimeMillis(),
            type = MaintenanceType.ORDINARY,
            description = "Manutenzione completata",
            mileage = mileage,
            cost = cost,
            notes = notes
        )
        viewModelScope.launch {
            repository.addMaintenanceRecord(record, vehicleId)
        }
    }
}

data class VehicleWithMaintenance(
    val vehicle: Vehicle,
    val maintenanceRecords: List<MaintenanceRecord>,
    val scheduledMaintenances: List<ScheduledMaintenance>,
    val upcomingMaintenances: List<ScheduledMaintenance>
) 