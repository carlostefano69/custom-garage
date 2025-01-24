package cg.customgarage.di

import android.content.Context
import cg.customgarage.data.local.dao.*
import cg.customgarage.data.repositories.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideVehicleRepository(
        vehicleDao: VehicleDao,
        maintenanceDao: MaintenanceDao
    ): VehicleRepository {
        return VehicleRepository(vehicleDao, maintenanceDao)
    }

    @Provides
    @Singleton
    fun provideProjectRepository(
        projectDao: ProjectDao,
        vehicleDao: VehicleDao,
        galleryDao: GalleryDao
    ): ProjectRepository {
        return ProjectRepository(projectDao, vehicleDao, galleryDao)
    }

    @Provides
    @Singleton
    fun provideGalleryRepository(
        @ApplicationContext context: Context,
        galleryDao: GalleryDao
    ): GalleryRepository {
        return GalleryRepository(context, galleryDao)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        settingsDao: SettingsDao
    ): SettingsRepository {
        return SettingsRepository(settingsDao)
    }
} 