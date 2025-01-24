package cg.customgarage.data.repositories

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import app.cash.turbine.test
import cg.customgarage.data.local.dao.GalleryDao
import cg.customgarage.data.models.GalleryImage
import cg.customgarage.data.models.ImageType
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream

class GalleryRepositoryTest {
    private lateinit var context: Context
    private lateinit var galleryDao: GalleryDao
    private lateinit var repository: GalleryRepository
    private lateinit var contentResolver: ContentResolver
    private lateinit var mockUri: Uri
    private lateinit var mockFile: File

    @Before
    fun setup() {
        context = mockk()
        galleryDao = mockk()
        contentResolver = mockk()
        mockUri = mockk()
        mockFile = mockk()
        
        every { context.contentResolver } returns contentResolver
        every { context.filesDir } returns File("test")
        
        repository = GalleryRepository(context, galleryDao)
    }

    @Test
    fun `getAllImages returns mapped images`() = runTest {
        // Given
        val image = GalleryImage(
            id = "1",
            uri = "test/image.jpg",
            type = ImageType.PROJECT,
            referenceId = "project1",
            description = "Test Image"
        )
        coEvery { galleryDao.getAllImages() } returns flowOf(listOf(image.toEntity()))

        // When & Then
        repository.getAllImages().test {
            val images = awaitItem()
            assertEquals(1, images.size)
            assertEquals("test/image.jpg", images[0].uri)
            awaitComplete()
        }
    }

    @Test
    fun `getImagesByType returns filtered images`() = runTest {
        // Given
        val image = GalleryImage(
            id = "1",
            uri = "test/image.jpg",
            type = ImageType.PROJECT,
            referenceId = "project1"
        )
        coEvery { galleryDao.getImagesByType(ImageType.PROJECT.name) } returns 
            flowOf(listOf(image.toEntity()))

        // When & Then
        repository.getImagesByType(ImageType.PROJECT).test {
            val images = awaitItem()
            assertEquals(1, images.size)
            assertEquals(ImageType.PROJECT, images[0].type)
            awaitComplete()
        }
    }
} 