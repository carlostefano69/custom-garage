package cg.customgarage.data.repositories

import android.content.Context
import android.net.Uri
import cg.customgarage.data.local.dao.GalleryDao
import cg.customgarage.data.local.entities.GalleryImageEntity
import cg.customgarage.data.models.GalleryImage
import cg.customgarage.data.models.ImageType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.util.*

class GalleryRepository(
    private val context: Context,
    private val galleryDao: GalleryDao
) {
    fun getAllImages(): Flow<List<GalleryImage>> {
        return galleryDao.getAllImages().map { entities ->
            entities.map { it.toModel() }
        }
    }

    fun getImagesByType(type: ImageType): Flow<List<GalleryImage>> {
        return galleryDao.getImagesByType(type.name).map { entities ->
            entities.map { it.toModel() }
        }
    }

    suspend fun addImage(uri: Uri, type: ImageType, referenceId: String? = null, description: String = "") {
        // Copia l'immagine nella directory interna dell'app
        val fileName = "img_${UUID.randomUUID()}.jpg"
        val file = File(context.filesDir, fileName)
        
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        val image = GalleryImage(
            uri = file.absolutePath,
            type = type,
            referenceId = referenceId,
            description = description
        )

        galleryDao.insertImage(image.toEntity())
    }

    suspend fun deleteImage(image: GalleryImage) {
        // Elimina il file
        File(image.uri).delete()
        galleryDao.deleteImage(image.toEntity())
    }

    private fun GalleryImageEntity.toModel() = GalleryImage(
        id = id,
        uri = uri,
        type = type,
        referenceId = referenceId,
        description = description,
        uploadDate = uploadDate
    )

    private fun GalleryImage.toEntity() = GalleryImageEntity(
        id = id,
        uri = uri,
        type = type,
        referenceId = referenceId,
        description = description,
        uploadDate = uploadDate
    )
} 