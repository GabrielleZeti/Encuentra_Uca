package com.example.encuentra_uca.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.encuentra_uca.data.remote.dto.ItemDto
import com.example.encuentra_uca.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val items: List<ItemDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedCategory: String? = null,
    val selectedType: String = "found"
)

val CATEGORIES = listOf("Todos", "💻 Electrónicos", "📄 Documentos", "🔑 Llaves", "🎒 Mochilas", "👕 Ropa", "📦 Otros")

class HomeViewModel(
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    fun loadItems(category: String? = null, type: String = "found") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = itemRepository.getItems(category, type)
            result.fold(
                onSuccess = { items ->
                    _uiState.value = _uiState.value.copy(items = items, isLoading = false)
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Error al cargar los objetos")
                }
            )
        }
    }

    fun onCategorySelected(category: String) {
        val newCategory = if (category == "Todos") null else category
        _uiState.value = _uiState.value.copy(selectedCategory = if (category == "Todos") null else category)
        loadItems(newCategory, _uiState.value.selectedType)
    }

    fun onTypeSelected(type: String) {
        _uiState.value = _uiState.value.copy(selectedType = type, selectedCategory = null)
        loadItems(type = type)
    }

    fun refresh() {
        loadItems(type = _uiState.value.selectedType)
    }
}