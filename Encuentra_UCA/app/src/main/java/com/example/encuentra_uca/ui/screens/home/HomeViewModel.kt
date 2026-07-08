package com.example.encuentra_uca.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.encuentra_uca.data.remote.dto.ObjetoDto
import com.example.encuentra_uca.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EstadoUiInicio(
    val objetos: List<ObjetoDto> = emptyList(),
    val estaCargando: Boolean = false,
    val mensajeError: String? = null,
    val categoriaSeleccionada: String? = null,
    val tipoSeleccionado: String = "found"
)

val CATEGORIAS = listOf("Todos", "💻 Electrónicos", "📄 Documentos", "🔑 Llaves", "🎒 Mochilas", "👕 Ropa", "📦 Otros")

class ViewModelInicio(
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _estadoUi = MutableStateFlow(EstadoUiInicio())
    val estadoUi: StateFlow<EstadoUiInicio> = _estadoUi.asStateFlow()

    init {
        cargarObjetos()
    }

    private fun mapearCategoriaAlServidor(categoria: String?): String? {
        if (categoria == null || categoria == "Todos") return null
        return when {
            categoria.contains("Electrónicos") -> "electronics"
            categoria.contains("Documentos") -> "documents"
            categoria.contains("Llaves") -> "keys"
            categoria.contains("Mochilas") -> "backpacks"
            categoria.contains("Ropa") -> "clothing"
            categoria.contains("Otros") -> "others"
            else -> categoria.replace(Regex("[^\\p{L}\\s]"), "").trim().lowercase()
        }
    }

    private fun mapearTipo(tipo: String): String {
        return when (tipo.lowercase()) {
            "encontrado", "found" -> "found"
            "perdido", "lost" -> "lost"
            else -> "found"
        }
    }

    fun cargarObjetos(categoria: String? = null, tipo: String = "found") {
        viewModelScope.launch {
            _estadoUi.value = _estadoUi.value.copy(estaCargando = true, mensajeError = null)
            val categoriaTecnica = mapearCategoriaAlServidor(categoria)
            val tipoTecnico = mapearTipo(tipo)
            
            val resultado = itemRepository.obtenerObjetos(categoriaTecnica, tipoTecnico)
            resultado.fold(
                onSuccess = { objetos ->
                    _estadoUi.value = _estadoUi.value.copy(objetos = objetos, estaCargando = false)
                },
                onFailure = {
                    _estadoUi.value = _estadoUi.value.copy(estaCargando = false, mensajeError = "Error al cargar los objetos: ${it.message}")
                }
            )
        }
    }

    fun alSeleccionarCategoria(categoria: String) {
        _estadoUi.value = _estadoUi.value.copy(categoriaSeleccionada = if (categoria == "Todos") null else categoria)
        cargarObjetos(categoria, _estadoUi.value.tipoSeleccionado)
    }

    fun alSeleccionarTipo(tipo: String) {
        val tipoTecnico = mapearTipo(tipo)
        _estadoUi.value = _estadoUi.value.copy(tipoSeleccionado = tipoTecnico, categoriaSeleccionada = null)
        cargarObjetos(tipo = tipoTecnico)
    }

    fun actualizar() {
        cargarObjetos(categoria = _estadoUi.value.categoriaSeleccionada, tipo = _estadoUi.value.tipoSeleccionado)
    }
}
