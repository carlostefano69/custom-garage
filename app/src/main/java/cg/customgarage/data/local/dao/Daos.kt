package cg.customgarage.data.local.dao

import androidx.room.*
import cg.customgarage.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles")
    fun getAllVehicles(): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE id = :id")
    suspend fun getVehicleById(id: String): VehicleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity)

    @Delete
    suspend fun deleteVehicle(vehicle: VehicleEntity)
}

@Dao
interface MaintenanceDao {
    @Query("SELECT * FROM scheduled_maintenances WHERE vehicleId = :vehicleId")
    fun getScheduledMaintenances(vehicleId: String): Flow<List<ScheduledMaintenanceEntity>>

    @Query("SELECT * FROM maintenance_records WHERE vehicleId = :vehicleId")
    fun getMaintenanceRecords(vehicleId: String): Flow<List<MaintenanceRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduledMaintenance(maintenance: ScheduledMaintenanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenanceRecord(record: MaintenanceRecordEntity)
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Query("SELECT * FROM modifications WHERE projectId = :projectId")
    fun getProjectModifications(projectId: String): Flow<List<ModificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModification(modification: ModificationEntity)
}

@Dao
interface GalleryDao {
    @Query("SELECT * FROM gallery_images")
    fun getAllImages(): Flow<List<GalleryImageEntity>>

    @Query("SELECT * FROM gallery_images WHERE type = :type")
    fun getImagesByType(type: String): Flow<List<GalleryImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: GalleryImageEntity)

    @Delete
    suspend fun deleteImage(image: GalleryImageEntity)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings LIMIT 1")
    fun getSettings(): Flow<SettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSettings(settings: SettingsEntity)
} 