package com.example.encuentra_uca.data.repository

import com.example.encuentra_uca.data.local.GestorToken
import com.example.encuentra_uca.data.remote.ServicioApiAutenticacion
import com.example.encuentra_uca.data.remote.dto.RespuestaAutenticacion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class RepositorioAutenticacion(
    private val servicioApi: ServicioApiAutenticacion,
    private val gestorToken: GestorToken
) {
    suspend fun registrar(nombre: String, correo: String, contrasena: String): Result<RespuestaAutenticacion> {
        return try {
            val respuesta = servicioApi.registrar(nombre, correo, contrasena)
            gestorToken.guardarSesion(respuesta.token, respuesta.usuario.correo, respuesta.usuario.nombre)
            Result.success(respuesta)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun iniciarSesion(correo: String, contrasena: String): Result<RespuestaAutenticacion> {
        return try {
            val respuesta = servicioApi.iniciarSesion(correo, contrasena)
            gestorToken.guardarSesion(respuesta.token, respuesta.usuario.correo, respuesta.usuario.nombre)
            Result.success(respuesta)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cerrarSesion() {
        gestorToken.cerrarSesion()
    }

    fun obtenerFlujoToken() = gestorToken.flujoToken

    fun obtenerInformacionUsuario(): Flow<Pair<String, String>> {
        return combine(gestorToken.flujoNombreUsuario, gestorToken.flujoCorreoUsuario) { nombre, correo ->
            Pair(nombre ?: "", correo ?: "")
        }
    }
}