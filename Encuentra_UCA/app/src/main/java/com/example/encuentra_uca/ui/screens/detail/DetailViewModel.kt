package com.example.encuentra_uca.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.encuentra_uca.data.local.GestorToken
import com.example.encuentra_uca.data.remote.dto.ObjetoDto
import com.example.encuentra_uca.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class EstadoUiDetalle(
    val objeto: ObjetoDto? = null,
    val estaCargando: Boolean = false,
    val mensajeError: String? = null,
    val esPropietario: Boolean = false,
    val estaEliminado: Boolean = false,
    val estaEliminando: Boolean = false
)

class ViewModelDetalle(
    private val itemRepository: ItemRepository,
    private val gestorToken: GestorToken
) : ViewModel() {

    private val _estadoUi = MutableStateFlow(EstadoUiDetalle())
    val estadoUi: StateFlow<EstadoUiDetalle> = _estadoUi.asStateFlow()

    fun cargarObjeto(id: Int) {
        viewModelScope.launch {
            _estadoUi.value = EstadoUiDetalle(estaCargando = true)

            val correoUsuarioActual = gestorToken.flujoCorreoUsuario.first() ?: ""
            val resultado = itemRepository.obtenerObjetoPorId(id)

            resultado.fold(
                onSuccess = { objeto ->
                    _estadoUi.value = EstadoUiDetalle(
                        objeto = objeto,
                        esPropietario = objeto.encontradoPorEmail.trim().lowercase() == correoUsuarioActual.trim().lowercase()
                    )
                },
                onFailure = {
                    _estadoUi.value = EstadoUiDetalle(mensajeError = "No se pudo cargar el objeto")
                }
            )
        }
    }

    fun eliminarObjeto() {
        val objeto = _estadoUi.value.objeto ?: return
        viewModelScope.launch {
            _estadoUi.value = _estadoUi.value.copy(estaEliminando = true)
            val token = gestorToken.flujoToken.first() ?: return@launch
            val resultado = itemRepository.eliminarObjeto(token, objeto.id)
            resultado.fold(
                onSuccess = {
                    _estadoUi.value = _estadoUi.value.copy(estaEliminado = true, estaEliminando = false)
                },
                onFailure = {
                    _estadoUi.value = _estadoUi.value.copy(
                        estaEliminando = false,
                        mensajeError = "Error al eliminar el objeto"
                    )
                }
            )
        }
    }
}