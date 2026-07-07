package com.example.encuentra_uca.ui.screens.publish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.encuentra_uca.data.local.TokenManager
import com.example.encuentra_uca.data.remote.dto.CreateItemRequest
import com.example.encuentra_uca.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class PublishUiState(
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val location: String = "",
    val type: String = "found",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isPublishSuccessful: Boolean = false
)

class PublishViewModel(
    private val itemRepository: ItemRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublishUiState())
    val uiState: StateFlow<PublishUiState> = _uiState.asStateFlow()

    fun onTitleChange(title: String) {
        _uiState.value = _uiState.value.copy(title = title, errorMessage = null)
    }

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(description = description, errorMessage = null)
    }

    fun onCategoryChange(category: String) {
        _uiState.value = _uiState.value.copy(category = category, errorMessage = null)
    }

    fun onLocationChange(location: String) {
        _uiState.value = _uiState.value.copy(location = location, errorMessage = null)
    }

    fun onTypeChange(type: String) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    fun publish() {
        val state = _uiState.value

        if (
            state.title.isBlank() ||
            state.description.isBlank() ||
            state.category.isBlank() ||
            state.location.isBlank()
        ) {
            _uiState.value = state.copy(errorMessage = "Completa todos los campos")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(
                isLoading = true,
                errorMessage = null
            )

            val token = tokenManager.tokenFlow.first()

            if (token.isNullOrBlank()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Sesion expirada, vuelve a iniciar sesión"
                )
                return@launch
            }

            val result = itemRepository.createItem(
                token = token,
                request = CreateItemRequest(
                    title = state.title,
                    description = state.description,
                    category = state.category,
                    location = state.location,
                    type = state.type
                )
            )

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isPublishSuccessful = true
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al publicar el objeto"
                    )
                }
            )
        }
    }
}