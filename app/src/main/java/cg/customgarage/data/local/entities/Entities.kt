package cg.customgarage.data.local.entities

import androidx.room.*
import cg.customgarage.data.models.*

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey val id: String,
    val name: String,
    val make: String,
    val model: String,
    val year: Int,
    val imageUrl: String?
)

@Entity(tableName = "scheduled_maintenances")
data class ScheduledMaintenanceEntity(
    @PrimaryKey val id: String,
    val vehicleId: String,
    @TypeConverters(MaintenanceTypeConverter::class)
    val type: MaintenanceType,
    val description: String,
    val intervalMonths: Int,
    val intervalMileage: Int,
    val estimatedCost: Double?,
    val nextDueDate: Long?,
    val nextDueMileage: Int?
)

@Entity(tableName = "maintenance_records")
data class MaintenanceRecordEntity(
    @PrimaryKey val id: String,
    val vehicleId: String,
    val date: Long,
    @TypeConverters(MaintenanceTypeConverter::class)
    val type: MaintenanceType,
    val description: String,
    val mileage: Int,
    val cost: Double,
    val notes: String
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val vehicleId: String,
    @TypeConverters(ProjectStatusConverter::class)
    val status: ProjectStatus,
    val createdAt: Long
)

@Entity(tableName = "modifications")
data class ModificationEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val name: String,
    val description: String,
    val cost: Double,
    @TypeConverters(ModificationStatusConverter::class)
    val status: ModificationStatus
)

@Entity(tableName = "gallery_images")
data class GalleryImageEntity(
    @PrimaryKey val id: String,
    val uri: String,
    @TypeConverters(ImageTypeConverter::class)
    val type: ImageType,
    val referenceId: String?,
    val description: String,
    val uploadDate: Long
)

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val darkMode: Boolean,
    val maintenanceNotifications: Boolean,
    val projectNotifications: Boolean,
    val language: String
) 