package cg.customgarage.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cg.customgarage.data.models.GalleryImage
import cg.customgarage.data.models.ImageType
import cg.customgarage.data.repositories.GalleryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: GalleryRepository
) : ViewModel() {

    private val _currentFilter = MutableStateFlow<ImageType?>(null)
    val currentFilter = _currentFilter.asStateFlow()

    val images: StateFlow<List<GalleryImage>> = combine(
        repository.getAllImages(),
        _currentFilter
    ) { images, filter ->
        when (filter) {
            null -> images
            else -> images.filter { it.type == filter }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setFilter(type: ImageType?) {
        _currentFilter.value = type
    }

    fun addImage(
        uri: Uri,
        type: ImageType,
        referenceId: String? = null,
        description: String = ""
    ) {
        viewModelScope.launch {
            repository.addImage(
                uri = uri,
                type = type,
                referenceId = referenceId,
                description = description
            )
        }
    }

    fun deleteImage(image: GalleryImage) {
        viewModelScope.launch {
            repository.deleteImage(image)
        }
    }
} 