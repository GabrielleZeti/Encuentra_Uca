package com.example.encuentra_uca.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ObjetoDto(
    val id: Int,
    @SerialName("title") val titulo: String,
    @SerialName("description") val descripcion: String,
    @SerialName("category") val categoria: String,
    @SerialName("imageUrl") val urlImagen: String,
    @SerialName("location") val ubicacion: String,
    @SerialName("foundById") val encontradoPorId: Int,
    @SerialName("foundByEmail") val encontradoPorEmail: String,
    @SerialName("status") val estado: String,
    @SerialName("type") val tipo: String = "found",
    @SerialName("timestamp") val marcaTiempo: Long
)

@Serializable
data class SolicitudCrearObjeto(
    @SerialName("title") val titulo: String,
    @SerialName("description") val descripcion: String,
    @SerialName("category") val categoria: String,
    @SerialName("location") val ubicacion: String,
    @SerialName("imageUrl") val urlImagen: String = "",
    @SerialName("type") val tipo: String = "found"
)