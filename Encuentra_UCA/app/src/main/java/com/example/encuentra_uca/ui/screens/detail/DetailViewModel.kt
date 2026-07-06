package com.example.encuentra_uca.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.encuentra_uca.data.local.TokenManager
import com.example.encuentra_uca.data.remote.dto.ItemDto
import com.example.encuentra_uca.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class DetailUiState(
    val item: ItemDto? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOwner: Boolean = false,
    val isDeleted: Boolean = false,
    val isDeleting: Boolean = false
)

class DetailViewModel(
    private val itemRepository: ItemRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadItem(id: Int) {
        viewModelScope.launch {
            _uiState.value = DetailUiState(isLoading = true)

            val currentUserEmail = tokenManager.userEmailFlow.first() ?: ""
            val result = itemRepository.getItemById(id)

            result.fold(
                onSuccess = { item ->
                    _uiState.value = DetailUiState(
                        item = item,
                        isOwner = item.foundByEmail == currentUserEmail
                    )
                },
                onFailure = {
                    _uiState.value = DetailUiState(errorMessage = "No se pudo cargar el objeto")
                }
            )
        }
    }

    fun deleteItem() {
        val item = _uiState.value.item ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeleting = true)
            val token = tokenManager.tokenFlow.first() ?: return@launch
            val result = itemRepository.deleteItem(token, item.id)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isDeleted = true, isDeleting = false)
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        isDeleting = false,
                        errorMessage = "Error al eliminar el objeto"
                    )
                }
            )
        }
    }
}
