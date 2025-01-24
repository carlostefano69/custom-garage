package cg.customgarage.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cg.customgarage.data.local.dao.*
import cg.customgarage.data.local.entities.*
import cg.customgarage.data.models.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    private lateinit var db: AppDatabase
    private lateinit var vehicleDao: VehicleDao
    private lateinit var maintenanceDao: MaintenanceDao
    private lateinit var projectDao: ProjectDao
    private lateinit var galleryDao: GalleryDao
    private lateinit var settingsDao: SettingsDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        vehicleDao = db.vehicleDao()
        maintenanceDao = db.maintenanceDao()
        projectDao = db.projectDao()
        galleryDao = db.galleryDao()
        settingsDao = db.settingsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadVehicle() = runBlocking {
        val vehicle = VehicleEntity(
            id = "1",
            name = "Test Vehicle",
            make = "Test Make",
            model = "Test Model",
            year = 2023
        )
        vehicleDao.insertVehicle(vehicle)
        val vehicles = vehicleDao.getAllVehicles().first()
        assertEquals(vehicle.name, vehicles[0].name)
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadProject() = runBlocking {
        val project = ProjectEntity(
            id = "1",
            name = "Test Project",
            description = "Test Description",
            vehicleId = "vehicle1",
            status = ProjectStatus.PLANNED
        )
        projectDao.insertProject(project)
        val projects = projectDao.getAllProjects().first()
        assertEquals(project.name, projects[0].name)
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadSettings() = runBlocking {
        val settings = SettingsEntity(
            darkMode = true,
            maintenanceNotifications = true,
            projectNotifications = false,
            language = "it"
        )
        settingsDao.updateSettings(settings)
        val result = settingsDao.getSettings().first()
        assertEquals(settings.language, result?.language)
    }
} 