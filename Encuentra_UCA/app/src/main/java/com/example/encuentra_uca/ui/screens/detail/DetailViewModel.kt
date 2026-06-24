package com.example.encuentra_uca.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.encuentra_uca.data.remote.dto.ItemDto
import com.example.encuentra_uca.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetailUiState(
    val item: ItemDto? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DetailViewModel(
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadItem(id: Int) {
        viewModelScope.launch {
            _uiState.value = DetailUiState(isLoading = true)

            val result = itemRepository.getItemById(id)

            result.fold(
                onSuccess = { item ->
                    _uiState.value = DetailUiState(item = item)
                },
                onFailure = {
                    _uiState.value = DetailUiState(errorMessage = "No se pudo cargar el objeto")
                }
            )
        }
    }
}