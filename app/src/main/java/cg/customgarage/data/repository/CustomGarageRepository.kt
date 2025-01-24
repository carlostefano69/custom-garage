package cg.customgarage.data.repository

import cg.customgarage.data.models.*
import kotlinx.coroutines.flow.Flow

interface CustomGarageRepository {
    // Veicoli
    suspend fun addVehicle(vehicle: Vehicle)
    suspend fun updateVehicle(vehicle: Vehicle)
    suspend fun deleteVehicle(vehicleId: String)
    fun getVehicle(vehicleId: String): Flow<Vehicle?>
    fun getAllVehicles(): Flow<List<Vehicle>>
    
    // Progetti
    suspend fun addProject(project: Project)
    suspend fun updateProject(project: Project)
    suspend fun deleteProject(projectId: String)
    fun getProject(projectId: String): Flow<Project?>
    fun getAllProjects(): Flow<List<Project>>
    fun getProjectsByStatus(status: ProjectStatus): Flow<List<Project>>
    
    // Modifiche
    suspend fun addModification(projectId: String, modification: Modification)
    suspend fun updateModification(projectId: String, modification: Modification)
    suspend fun deleteModification(projectId: String, modificationId: String)
    
    // Costi
    suspend fun addCost(projectId: String, cost: Cost)
    suspend fun deleteCost(projectId: String, costId: String)
    fun getProjectCosts(projectId: String): Flow<List<Cost>>
    
    // Galleria
    suspend fun addInspirationImage(projectId: String, imageUrl: String)
    suspend fun deleteInspirationImage(projectId: String, imageUrl: String)
    fun getProjectInspirationImages(projectId: String): Flow<List<String>>
    fun getAllInspirationImages(): Flow<List<String>>
    
    // Manutenzione
    suspend fun addMaintenanceRecord(vehicleId: String, record: MaintenanceRecord)
    suspend fun updateMaintenanceRecord(vehicleId: String, record: MaintenanceRecord)
    suspend fun deleteMaintenanceRecord(vehicleId: String, recordId: String)
    fun getMaintenanceRecords(vehicleId: String): Flow<List<MaintenanceRecord>>
    
    suspend fun addScheduledMaintenance(vehicleId: String, maintenance: ScheduledMaintenance)
    suspend fun updateScheduledMaintenance(vehicleId: String, maintenance: ScheduledMaintenance)
    suspend fun deleteScheduledMaintenance(vehicleId: String, maintenanceId: String)
    fun getScheduledMaintenances(vehicleId: String): Flow<List<ScheduledMaintenance>>
    fun getUpcomingMaintenances(vehicleId: String): Flow<List<ScheduledMaintenance>>
} 