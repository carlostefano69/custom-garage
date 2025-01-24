package cg.customgarage.data.local.converters

import androidx.room.TypeConverter
import cg.customgarage.data.models.*

class MaintenanceTypeConverter {
    @TypeConverter
    fun fromMaintenanceType(type: MaintenanceType): String = type.name

    @TypeConverter
    fun toMaintenanceType(value: String): MaintenanceType = 
        MaintenanceType.valueOf(value)
}

class ProjectStatusConverter {
    @TypeConverter
    fun fromProjectStatus(status: ProjectStatus): String = status.name

    @TypeConverter
    fun toProjectStatus(value: String): ProjectStatus = 
        ProjectStatus.valueOf(value)
}

class ModificationStatusConverter {
    @TypeConverter
    fun fromModificationStatus(status: ModificationStatus): String = status.name

    @TypeConverter
    fun toModificationStatus(value: String): ModificationStatus = 
        ModificationStatus.valueOf(value)
}

class ImageTypeConverter {
    @TypeConverter
    fun fromImageType(type: ImageType): String = type.name

    @TypeConverter
    fun toImageType(value: String): ImageType = 
        ImageType.valueOf(value)
}

class StringListConverter {
    @TypeConverter
    fun fromString(value: String): List<String> = 
        if (value.isBlank()) emptyList() else value.split(",")

    @TypeConverter
    fun toString(list: List<String>): String = list.joinToString(",")
} 