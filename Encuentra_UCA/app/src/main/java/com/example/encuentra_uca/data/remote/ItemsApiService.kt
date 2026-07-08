package com.example.encuentra_uca.data.remote

import com.example.encuentra_uca.data.remote.dto.SolicitudCrearObjeto
import com.example.encuentra_uca.data.remote.dto.ObjetoDto
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType


class ServicioApiObjetos {
    private val cliente = ApiClient.clienteHttp
    private val urlBase = ApiClient.URL_BASE

    suspend fun obtenerObjetos(categoria: String? = null, tipo: String = "found"): List<ObjetoDto> {
        var url = "$urlBase/items?type=$tipo"
        if (categoria != null) url += "&category=$categoria"
        return cliente.get(url).body()
    }

    suspend fun obtenerObjetoPorId(id: Int): ObjetoDto {
        return cliente.get("$urlBase/items/$id").body()
    }

    suspend fun crearObjeto(token: String, solicitud: SolicitudCrearObjeto): ObjetoDto {
        return cliente.post("$urlBase/items") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(solicitud)
        }.body()
    }

    suspend fun eliminarObjeto(token: String, id: Int) {
        cliente.delete("$urlBase/items/$id") {
            header("Authorization", "Bearer $token")
        }
    }
}
