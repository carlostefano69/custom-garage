package cg.customgarage.data.repository

import cg.customgarage.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class InMemoryCustomGarageRepository : CustomGarageRepository {
    private val vehicles = MutableStateFlow<Map<String, Vehicle>>(emptyMap())
    private val projects = MutableStateFlow<Map<String, Project>>(emptyMap())
    private val inspirationImages = MutableStateFlow<List<String>>(emptyList())

    override suspend fun addVehicle(vehicle: Vehicle) {
        vehicles.update { it + (vehicle.id to vehicle) }
    }

    override suspend fun updateVehicle(vehicle: Vehicle) {
        vehicles.update { it + (vehicle.id to vehicle) }
    }

    override suspend fun deleteVehicle(vehicleId: String) {
        vehicles.update { it - vehicleId }
    }

    override fun getVehicle(vehicleId: String): Flow<Vehicle?> {
        return vehicles.map { it[vehicleId] }
    }

    override fun getAllVehicles(): Flow<List<Vehicle>> {
        return vehicles.map { it.values.toList() }
    }

    override suspend fun addProject(project: Project) {
        projects.update { it + (project.id to project) }
    }

    override suspend fun updateProject(project: Project) {
        projects.update { it + (project.id to project) }
    }

    override suspend fun deleteProject(projectId: String) {
        projects.update { it - projectId }
    }

    override fun getProject(projectId: String): Flow<Project?> {
        return projects.map { it[projectId] }
    }

    override fun getAllProjects(): Flow<List<Project>> {
        return projects.map { it.values.toList() }
    }

    override fun getProjectsByStatus(status: ProjectStatus): Flow<List<Project>> {
        return projects.map { projects ->
            projects.values.filter { it.status == status }
        }
    }

    override suspend fun addModification(projectId: String, modification: Modification) {
        projects.update { projects ->
            projects[projectId]?.let { project ->
                projects + (project.id to project.copy(
                    modifications = project.modifications + modification
                ))
            } ?: projects
        }
    }

    override suspend fun updateModification(projectId: String, modification: Modification) {
        projects.update { projects ->
            projects[projectId]?.let { project ->
                projects + (project.id to project.copy(
                    modifications = project.modifications.map { 
                        if (it.id == modification.id) modification else it 
                    }
                ))
            } ?: projects
        }
    }

    override suspend fun deleteModification(projectId: String, modificationId: String) {
        projects.update { projects ->
            projects[projectId]?.let { project ->
                projects + (project.id to project.copy(
                    modifications = project.modifications.filterNot { it.id == modificationId }
                ))
            } ?: projects
        }
    }

    override suspend fun addCost(projectId: String, cost: Cost) {
        projects.update { projects ->
            projects[projectId]?.let { project ->
                projects + (project.id to project.copy(
                    costs = project.costs + cost
                ))
            } ?: projects
        }
    }

    override suspend fun deleteCost(projectId: String, costId: String) {
        projects.update { projects ->
            projects[projectId]?.let { project ->
                projects + (project.id to project.copy(
                    costs = project.costs.filterNot { it.id == costId }
                ))
            } ?: projects
        }
    }

    override fun getProjectCosts(projectId: String): Flow<List<Cost>> {
        return projects.map { it[projectId]?.costs ?: emptyList() }
    }

    override suspend fun addInspirationImage(projectId: String, imageUrl: String) {
        projects.update { projects ->
            projects[projectId]?.let { project ->
                projects + (project.id to project.copy(
                    inspirationImages = project.inspirationImages + imageUrl
                ))
            } ?: projects
        }
        inspirationImages.update { it + imageUrl }
    }

    override suspend fun deleteInspirationImage(projectId: String, imageUrl: String) {
        projects.update { projects ->
            projects[projectId]?.let { project ->
                projects + (project.id to project.copy(
                    inspirationImages = project.inspirationImages - imageUrl
                ))
            } ?: projects
        }
        inspirationImages.update { it - imageUrl }
    }

    override fun getProjectInspirationImages(projectId: String): Flow<List<String>> {
        return projects.map { it[projectId]?.inspirationImages ?: emptyList() }
    }

    override fun getAllInspirationImages(): Flow<List<String>> {
        return inspirationImages
    }

    override suspend fun addMaintenanceRecord(vehicleId: String, record: MaintenanceRecord) {
        vehicles.update { vehicles ->
            vehicles[vehicleId]?.let { vehicle ->
                vehicles + (vehicle.id to vehicle.copy(
                    maintenanceRecords = vehicle.maintenanceRecords + record
                ))
            } ?: vehicles
        }
    }

    override suspend fun updateMaintenanceRecord(vehicleId: String, record: MaintenanceRecord) {
        vehicles.update { vehicles ->
            vehicles[vehicleId]?.let { vehicle ->
                vehicles + (vehicle.id to vehicle.copy(
                    maintenanceRecords = vehicle.maintenanceRecords.map { 
                        if (it.id == record.id) record else it 
                    }
                ))
            } ?: vehicles
        }
    }

    override suspend fun deleteMaintenanceRecord(vehicleId: String, recordId: String) {
        vehicles.update { vehicles ->
            vehicles[vehicleId]?.let { vehicle ->
                vehicles + (vehicle.id to vehicle.copy(
                    maintenanceRecords = vehicle.maintenanceRecords.filterNot { it.id == recordId }
                ))
            } ?: vehicles
        }
    }

    override fun getMaintenanceRecords(vehicleId: String): Flow<List<MaintenanceRecord>> {
        return vehicles.map { it[vehicleId]?.maintenanceRecords ?: emptyList() }
    }

    override suspend fun addScheduledMaintenance(vehicleId: String, maintenance: ScheduledMaintenance) {
        vehicles.update { vehicles ->
            vehicles[vehicleId]?.let { vehicle ->
                vehicles + (vehicle.id to vehicle.copy(
                    scheduledMaintenances = vehicle.scheduledMaintenances + maintenance
                ))
            } ?: vehicles
        }
    }

    override suspend fun updateScheduledMaintenance(vehicleId: String, maintenance: ScheduledMaintenance) {
        vehicles.update { vehicles ->
            vehicles[vehicleId]?.let { vehicle ->
                vehicles + (vehicle.id to vehicle.copy(
                    scheduledMaintenances = vehicle.scheduledMaintenances.map { 
                        if (it.id == maintenance.id) maintenance else it 
                    }
                ))
            } ?: vehicles
        }
    }

    override suspend fun deleteScheduledMaintenance(vehicleId: String, maintenanceId: String) {
        vehicles.update { vehicles ->
            vehicles[vehicleId]?.let { vehicle ->
                vehicles + (vehicle.id to vehicle.copy(
                    scheduledMaintenances = vehicle.scheduledMaintenances.filterNot { it.id == maintenanceId }
                ))
            } ?: vehicles
        }
    }

    override fun getScheduledMaintenances(vehicleId: String): Flow<List<ScheduledMaintenance>> {
        return vehicles.map { it[vehicleId]?.scheduledMaintenances ?: emptyList() }
    }

    override fun getUpcomingMaintenances(vehicleId: String): Flow<List<ScheduledMaintenance>> {
        val currentTime = System.currentTimeMillis()
        return vehicles.map { vehicles ->
            vehicles[vehicleId]?.scheduledMaintenances?.filter { maintenance ->
                maintenance.nextDueDate?.let { it > currentTime } ?: false
            } ?: emptyList()
        }
    }
} 