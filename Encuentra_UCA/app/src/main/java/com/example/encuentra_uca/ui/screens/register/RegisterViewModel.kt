package com.example.encuentra_uca.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.encuentra_uca.data.repository.RepositorioAutenticacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EstadoUiRegistro(
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",
    val estaCargando: Boolean = false,
    val mensajeError: String? = null,
    val registroExitoso: Boolean = false
)

class ViewModelRegistro(
    private val repositorioAutenticacion: RepositorioAutenticacion
) : ViewModel() {

    private val _estadoUi = MutableStateFlow(EstadoUiRegistro())
    val estadoUi: StateFlow<EstadoUiRegistro> = _estadoUi.asStateFlow()

    fun alCambiarNombre(nombre: String) {
        _estadoUi.value = _estadoUi.value.copy(nombre = nombre, mensajeError = null)
    }

    fun alCambiarCorreo(correo: String) {
        _estadoUi.value = _estadoUi.value.copy(correo = correo, mensajeError = null)
    }

    fun alCambiarContrasena(contrasena: String) {
        _estadoUi.value = _estadoUi.value.copy(contrasena = contrasena, mensajeError = null)
    }

    fun alCambiarConfirmarContrasena(confirmarContrasena: String) {
        _estadoUi.value = _estadoUi.value.copy(confirmarContrasena = confirmarContrasena, mensajeError = null)
    }

    fun registrar() {
        val estado = _estadoUi.value

        if (estado.nombre.isBlank() || estado.correo.isBlank() || estado.contrasena.isBlank()) {
            _estadoUi.value = estado.copy(mensajeError = "Completa todos los campos")
            return
        }

        if (!estado.correo.endsWith("@uca.edu.sv")) {
            _estadoUi.value = estado.copy(mensajeError = "Usa tu correo institucional (@uca.edu.sv)")
            return
        }

        if (estado.contrasena != estado.confirmarContrasena) {
            _estadoUi.value = estado.copy(mensajeError = "Las contraseñas no coinciden")
            return
        }

        if (estado.contrasena.length < 6) {
            _estadoUi.value = estado.copy(mensajeError = "La contraseña debe tener al menos 6 caracteres")
            return
        }

        viewModelScope.launch {
            _estadoUi.value = estado.copy(estaCargando = true, mensajeError = null)

            val resultado = repositorioAutenticacion.registrar(estado.nombre, estado.correo, estado.contrasena)

            resultado.fold(
                onSuccess = {
                    _estadoUi.value = _estadoUi.value.copy(
                        estaCargando = false,
                        registroExitoso = true
                    )
                },
                onFailure = {
                    _estadoUi.value = _estadoUi.value.copy(
                        estaCargando = false,
                        mensajeError = "Error al registrarse. El correo ya puede estar en uso."
                    )
                }
            )
        }
    }
}