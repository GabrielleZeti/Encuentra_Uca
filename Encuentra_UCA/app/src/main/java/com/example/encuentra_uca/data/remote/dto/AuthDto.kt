package com.example.encuentra_uca.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SolicitudRegistro(
    @SerialName("name") val nombre: String,
    @SerialName("email") val correo: String,
    @SerialName("password") val contrasena: String
)

@Serializable
data class SolicitudInicioSesion(
    @SerialName("email") val correo: String,
    @SerialName("password") val contrasena: String
)

@Serializable
data class RespuestaAutenticacion(
    @SerialName("token") val token: String,
    @SerialName("user") val usuario: UsuarioDto
)

@Serializable
data class UsuarioDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val nombre: String,
    @SerialName("email") val correo: String
)

@Serializable
data class RespuestaError(
    @SerialName("error") val error: String
)