package cg.customgarage.ui.viewmodels

import app.cash.turbine.test
import cg.customgarage.data.models.*
import cg.customgarage.data.repositories.ProjectRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProjectViewModelTest {
    private lateinit var repository: ProjectRepository
    private lateinit var viewModel: ProjectViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = ProjectViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `projects state is updated when repository emits new data`() = runTest {
        // Given
        val testProject = Project(
            id = "1",
            name = "Test Project",
            description = "Test Description",
            vehicleId = "vehicle1",
            status = ProjectStatus.PLANNED
        )
        coEvery { repository.getAllProjects() } returns flowOf(listOf(testProject))

        // When & Then
        viewModel.projects.test {
            val projects = awaitItem()
            assertEquals(1, projects.size)
            assertEquals("Test Project", projects[0].name)
        }
    }

    @Test
    fun `getProjectDetails returns correct project details`() = runTest {
        // Given
        val projectDetails = ProjectDetails(
            project = Project(
                id = "1",
                name = "Test Project",
                description = "Test Description",
                vehicleId = "vehicle1",
                status = ProjectStatus.IN_PROGRESS
            ),
            totalCost = 1000.0,
            completedModifications = 2,
            inspirationImages = listOf("image1.jpg", "image2.jpg")
        )
        coEvery { repository.getProjectDetails("1") } returns flowOf(projectDetails)

        // When & Then
        viewModel.getProjectDetails("1").test {
            val details = awaitItem()
            assertEquals("Test Project", details?.project?.name)
            assertEquals(1000.0, details?.totalCost)
            assertEquals(2, details?.completedModifications)
        }
    }

    @Test
    fun `updateModificationStatus calls repository with correct data`() = runTest {
        // Given
        coEvery { repository.updateModificationStatus(any(), any(), any()) } returns Unit

        // When
        viewModel.updateModificationStatus(
            projectId = "1",
            modificationId = "mod1",
            status = ModificationStatus.COMPLETED
        )

        // Then
        coVerify { repository.updateModificationStatus(
            projectId = "1",
            modificationId = "mod1",
            status = ModificationStatus.COMPLETED
        )}
    }
} 