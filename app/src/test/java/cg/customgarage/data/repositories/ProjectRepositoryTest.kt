package cg.customgarage.data.repositories

import app.cash.turbine.test
import cg.customgarage.data.local.dao.GalleryDao
import cg.customgarage.data.local.dao.ProjectDao
import cg.customgarage.data.local.dao.VehicleDao
import cg.customgarage.data.models.Project
import cg.customgarage.data.models.ProjectStatus
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProjectRepositoryTest {
    private lateinit var projectDao: ProjectDao
    private lateinit var vehicleDao: VehicleDao
    private lateinit var galleryDao: GalleryDao
    private lateinit var repository: ProjectRepository

    @Before
    fun setup() {
        projectDao = mockk()
        vehicleDao = mockk()
        galleryDao = mockk()
        repository = ProjectRepository(projectDao, vehicleDao, galleryDao)
    }

    @Test
    fun `getAllProjects returns mapped projects`() = runTest {
        // Given
        val project = Project(
            id = "1",
            name = "Test Project",
            description = "Test Description",
            vehicleId = "vehicle1",
            status = ProjectStatus.PLANNED
        )
        coEvery { projectDao.getAllProjects() } returns flowOf(listOf(project.toEntity()))

        // When & Then
        repository.getAllProjects().test {
            val projects = awaitItem()
            assertEquals(1, projects.size)
            assertEquals("Test Project", projects[0].name)
            awaitComplete()
        }
    }

    @Test
    fun `addProject correctly stores project and modifications`() = runTest {
        // Given
        val project = Project(
            id = "1",
            name = "Test Project",
            description = "Test Description",
            vehicleId = "vehicle1",
            status = ProjectStatus.PLANNED
        )
        coEvery { projectDao.insertProject(any()) } returns Unit
        coEvery { projectDao.insertModification(any()) } returns Unit

        // When
        repository.addProject(project)

        // Then
        coEvery { projectDao.getProjectById("1") } returns project.toEntity()
        val storedProject = projectDao.getProjectById("1")
        assertEquals(project.name, storedProject?.name)
    }
} 