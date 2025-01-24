package cg.customgarage.data.repositories

import cg.customgarage.data.local.dao.ProjectDao
import cg.customgarage.data.local.dao.VehicleDao
import cg.customgarage.data.local.dao.GalleryDao
import cg.customgarage.data.local.entities.*
import cg.customgarage.data.models.*
import kotlinx.coroutines.flow.*

class ProjectRepository(
    private val projectDao: ProjectDao,
    private val vehicleDao: VehicleDao,
    private val galleryDao: GalleryDao
) {
    fun getAllProjects(): Flow<List<Project>> {
        return projectDao.getAllProjects().map { entities ->
            entities.map { it.toModel() }
        }
    }

    fun getProjectDetails(projectId: String): Flow<ProjectDetails?> {
        return combine(
            flow { emit(projectDao.getProjectById(projectId)) },
            projectDao.getProjectModifications(projectId),
            galleryDao.getImagesByType(ImageType.PROJECT.name)
        ) { projectEntity, modifications, images ->
            projectEntity?.let { project ->
                val vehicle = vehicleDao.getVehicleById(project.vehicleId)
                    ?: return@combine null

                ProjectDetails(
                    project = project.toModel(modifications.map { it.toModel() }),
                    vehicle = vehicle.toModel(),
                    totalCost = modifications.sumOf { it.cost },
                    completedModifications = modifications.count { it.status == ModificationStatus.COMPLETED },
                    inspirationImages = images.filter { it.referenceId == projectId }.map { it.toModel() }
                )
            }
        }
    }

    suspend fun addProject(project: Project) {
        projectDao.insertProject(project.toEntity())
        project.modifications.forEach { modification ->
            projectDao.insertModification(modification.toEntity(project.id))
        }
    }

    suspend fun updateModificationStatus(
        projectId: String,
        modificationId: String,
        status: ModificationStatus
    ) {
        val modification = projectDao.getProjectModifications(projectId)
            .first()
            .find { it.id == modificationId }
            ?.copy(status = status)
            ?: return

        projectDao.insertModification(modification)
    }

    private fun ProjectEntity.toModel(modifications: List<Modification> = emptyList()) = Project(
        id = id,
        name = name,
        description = description,
        vehicleId = vehicleId,
        status = status,
        modifications = modifications,
        createdAt = createdAt
    )

    private fun Project.toEntity() = ProjectEntity(
        id = id,
        name = name,
        description = description,
        vehicleId = vehicleId,
        status = status,
        createdAt = createdAt
    )

    private fun ModificationEntity.toModel() = Modification(
        id = id,
        name = name,
        description = description,
        cost = cost,
        status = status
    )

    private fun Modification.toEntity(projectId: String) = ModificationEntity(
        id = id,
        projectId = projectId,
        name = name,
        description = description,
        cost = cost,
        status = status
    )

    private fun VehicleEntity.toModel() = Vehicle(
        id = id,
        name = name,
        make = make,
        model = model,
        year = year,
        imageUrl = imageUrl
    )

    private fun GalleryImageEntity.toModel() = GalleryImage(
        id = id,
        uri = uri,
        type = type,
        referenceId = referenceId,
        description = description,
        uploadDate = uploadDate
    )
} 