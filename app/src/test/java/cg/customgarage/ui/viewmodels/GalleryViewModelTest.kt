package cg.customgarage.ui.viewmodels

import android.net.Uri
import app.cash.turbine.test
import cg.customgarage.data.models.GalleryImage
import cg.customgarage.data.models.ImageType
import cg.customgarage.data.repositories.GalleryRepository
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
class GalleryViewModelTest {
    private lateinit var repository: GalleryRepository
    private lateinit var viewModel: GalleryViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = GalleryViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `images state is filtered when filter is set`() = runTest {
        // Given
        val projectImage = GalleryImage(
            id = "1",
            uri = "test/project.jpg",
            type = ImageType.PROJECT
        )
        val maintenanceImage = GalleryImage(
            id = "2",
            uri = "test/maintenance.jpg",
            type = ImageType.MAINTENANCE
        )
        coEvery { repository.getAllImages() } returns flowOf(listOf(projectImage, maintenanceImage))

        // When
        viewModel.setFilter(ImageType.PROJECT)

        // Then
        viewModel.images.test {
            val images = awaitItem()
            assertEquals(1, images.size)
            assertEquals(ImageType.PROJECT, images[0].type)
        }
    }

    @Test
    fun `addImage calls repository with correct data`() = runTest {
        // Given
        val uri = mockk<Uri>()
        coEvery { repository.addImage(any(), any(), any(), any()) } returns Unit

        // When
        viewModel.addImage(
            uri = uri,
            type = ImageType.PROJECT,
            referenceId = "project1",
            description = "Test Image"
        )

        // Then
        coVerify { repository.addImage(
            uri = uri,
            type = ImageType.PROJECT,
            referenceId = "project1",
            description = "Test Image"
        )}
    }
} 