package com.example.encuentra_uca.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.encuentra_uca.data.repository.RepositorioAutenticacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EstadoUiInicioSesion(
    val correo: String = "",
    val contrasena: String = "",
    val estaCargando: Boolean = false,
    val mensajeError: String? = null,
    val inicioSesionExitoso: Boolean = false
)

class ViewModelInicioSesion(
    private val repositorioAutenticacion: RepositorioAutenticacion
) : ViewModel() {

    private val _estadoUi = MutableStateFlow(EstadoUiInicioSesion())
    val estadoUi: StateFlow<EstadoUiInicioSesion> = _estadoUi.asStateFlow()

    fun alCambiarCorreo(correo: String) {
        _estadoUi.value = _estadoUi.value.copy(correo = correo, mensajeError = null)
    }

    fun alCambiarContrasena(contrasena: String) {
        _estadoUi.value = _estadoUi.value.copy(contrasena = contrasena, mensajeError = null)
    }

    fun iniciarSesion() {
        val correo = _estadoUi.value.correo
        val contrasena = _estadoUi.value.contrasena

        if (correo.isBlank() || contrasena.isBlank()) {
            _estadoUi.value = _estadoUi.value.copy(mensajeError = "Completa todos los campos")
            return
        }

        viewModelScope.launch {
            _estadoUi.value = _estadoUi.value.copy(estaCargando = true, mensajeError = null)

            val resultado = repositorioAutenticacion.iniciarSesion(correo, contrasena)

            resultado.fold(
                onSuccess = {
                    _estadoUi.value = _estadoUi.value.copy(
                        estaCargando = false,
                        inicioSesionExitoso = true
                    )
                },
                onFailure = { error ->
                    _estadoUi.value = _estadoUi.value.copy(
                        estaCargando = false,
                        mensajeError = "Credenciales inválidas o error de conexión"
                    )
                }
            )
        }
    }
}