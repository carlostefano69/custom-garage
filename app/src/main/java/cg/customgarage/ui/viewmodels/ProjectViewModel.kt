package cg.customgarage.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cg.customgarage.data.models.*
import cg.customgarage.data.repositories.ProjectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val repository: ProjectRepository
) : ViewModel() {

    val projects: StateFlow<List<Project>> = repository.getAllProjects()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getProjectDetails(projectId: String): StateFlow<ProjectDetails?> {
        return repository.getProjectDetails(projectId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    }

    fun addProject(
        name: String,
        description: String,
        vehicleId: String,
        modifications: List<Modification> = emptyList()
    ) {
        val project = Project(
            name = name,
            description = description,
            vehicleId = vehicleId,
            modifications = modifications
        )
        viewModelScope.launch {
            repository.addProject(project)
        }
    }

    fun updateModificationStatus(
        projectId: String,
        modificationId: String,
        status: ModificationStatus
    ) {
        viewModelScope.launch {
            repository.updateModificationStatus(
                projectId = projectId,
                modificationId = modificationId,
                status = status
            )
        }
    }
}

data class ProjectDetails(
    val project: Project,
    val totalCost: Double,
    val completedModifications: Int,
    val inspirationImages: List<String>
) 