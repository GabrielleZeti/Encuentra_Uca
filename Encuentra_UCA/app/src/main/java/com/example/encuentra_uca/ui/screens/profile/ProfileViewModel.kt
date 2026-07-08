package com.example.encuentra_uca.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.encuentra_uca.data.repository.RepositorioAutenticacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EstadoUiPerfil(
    val nombreUsuario: String = "",
    val correoUsuario: String = "",
    val cierreSesionExitoso: Boolean = false
)

class ViewModelPerfil(
    private val repositorioAutenticacion: RepositorioAutenticacion
) : ViewModel() {

    private val _estadoUi = MutableStateFlow(EstadoUiPerfil())
    val estadoUi: StateFlow<EstadoUiPerfil> = _estadoUi.asStateFlow()

    init {
        cargarInformacionUsuario()
    }

    private fun cargarInformacionUsuario() {
        viewModelScope.launch {
            repositorioAutenticacion.obtenerInformacionUsuario().collect { (nombre, correo) ->
                _estadoUi.value = _estadoUi.value.copy(
                    nombreUsuario = nombre,
                    correoUsuario = correo
                )
            }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            repositorioAutenticacion.cerrarSesion()
            _estadoUi.value = _estadoUi.value.copy(cierreSesionExitoso = true)
        }
    }
}