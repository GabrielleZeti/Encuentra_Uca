package com.example.encuentra_uca.ui.screens.publish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.encuentra_uca.data.local.GestorToken
import com.example.encuentra_uca.data.remote.dto.SolicitudCrearObjeto
import com.example.encuentra_uca.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class EstadoUiPublicar(
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val ubicacion: String = "",
    val tipo: String = "found",
    val estaCargando: Boolean = false,
    val mensajeError: String? = null,
    val publicacionExitosa: Boolean = false
)

class ViewModelPublicar(
    private val itemRepository: ItemRepository,
    private val gestorToken: GestorToken
) : ViewModel() {

    private val _estadoUi = MutableStateFlow(EstadoUiPublicar())
    val estadoUi: StateFlow<EstadoUiPublicar> = _estadoUi.asStateFlow()

    fun alCambiarTitulo(titulo: String) {
        _estadoUi.value = _estadoUi.value.copy(titulo = titulo, mensajeError = null)
    }

    fun alCambiarDescripcion(descripcion: String) {
        _estadoUi.value = _estadoUi.value.copy(descripcion = descripcion, mensajeError = null)
    }

    fun alCambiarCategoria(categoria: String) {
        _estadoUi.value = _estadoUi.value.copy(categoria = categoria, mensajeError = null)
    }

    fun alCambiarUbicacion(ubicacion: String) {
        _estadoUi.value = _estadoUi.value.copy(ubicacion = ubicacion, mensajeError = null)
    }

    fun alCambiarTipo(tipo: String) {
        _estadoUi.value = _estadoUi.value.copy(tipo = tipo)
    }

    private fun mapearCategoriaAlServidor(categoria: String): String {
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
            "encontré algo", "encontrado", "found" -> "found"
            "perdí algo", "perdido", "lost" -> "lost"
            else -> "found"
        }
    }

    fun publicar() {
        val estado = _estadoUi.value

        if (
            estado.titulo.isBlank() ||
            estado.descripcion.isBlank() ||
            estado.categoria.isBlank() ||
            estado.ubicacion.isBlank()
        ) {
            _estadoUi.value = estado.copy(mensajeError = "Completa todos los campos")
            return
        }

        viewModelScope.launch {
            _estadoUi.value = estado.copy(
                estaCargando = true,
                mensajeError = null
            )

            val token = gestorToken.flujoToken.first()

            if (token.isNullOrBlank()) {
                _estadoUi.value = _estadoUi.value.copy(
                    estaCargando = false,
                    mensajeError = "Sesión expirada, vuelve a iniciar sesión"
                )
                return@launch
            }

            val resultado = itemRepository.crearObjeto(
                token = token,
                solicitud = SolicitudCrearObjeto(
                    titulo = estado.titulo,
                    descripcion = estado.descripcion,
                    categoria = mapearCategoriaAlServidor(estado.categoria),
                    ubicacion = estado.ubicacion,
                    tipo = mapearTipo(estado.tipo)
                )
            )

            resultado.fold(
                onSuccess = {
                    _estadoUi.value = _estadoUi.value.copy(
                        estaCargando = false,
                        publicacionExitosa = true
                    )
                },
                onFailure = {
                    _estadoUi.value = _estadoUi.value.copy(
                        estaCargando = false,
                        mensajeError = "Error al publicar: ${it.message}"
                    )
                }
            )
        }
    }
}
