package com.example.encuentra_uca.data.remote

import com.example.encuentra_uca.data.remote.dto.RespuestaAutenticacion
import com.example.encuentra_uca.data.remote.dto.SolicitudInicioSesion
import com.example.encuentra_uca.data.remote.dto.SolicitudRegistro
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ServicioApiAutenticacion {
    private val cliente = ApiClient.clienteHttp
    private val urlBase = ApiClient.URL_BASE

    suspend fun registrar(nombre: String, correo: String, contrasena: String): RespuestaAutenticacion {
        return cliente.post("$urlBase/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(SolicitudRegistro(nombre, correo, contrasena))
        }.body()
    }

    suspend fun iniciarSesion(correo: String, contrasena: String): RespuestaAutenticacion {
        return cliente.post("$urlBase/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(SolicitudInicioSesion(correo, contrasena))
        }.body()
    }
}
