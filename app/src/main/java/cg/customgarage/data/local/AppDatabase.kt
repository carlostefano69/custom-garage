package cg.customgarage.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cg.customgarage.data.local.converters.*
import cg.customgarage.data.local.dao.*
import cg.customgarage.data.local.entities.*

@Database(
    entities = [
        VehicleEntity::class,
        ScheduledMaintenanceEntity::class,
        MaintenanceRecordEntity::class,
        ProjectEntity::class,
        ModificationEntity::class,
        GalleryImageEntity::class,
        SettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    MaintenanceTypeConverter::class,
    ProjectStatusConverter::class,
    ModificationStatusConverter::class,
    ImageTypeConverter::class,
    StringListConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun maintenanceDao(): MaintenanceDao
    abstract fun projectDao(): ProjectDao
    abstract fun galleryDao(): GalleryDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "custom_garage_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 