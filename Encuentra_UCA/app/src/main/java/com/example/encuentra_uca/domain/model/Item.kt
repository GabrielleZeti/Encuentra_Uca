package com.example.encuentra_uca.domain.model

data class Item(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val urlImagen: String = "",
    val ubicacion: String = "",
    val encontradoPor: String = "",
    val encontradoPorEmail: String = "",
    val estado: String = "available",
    val marcaTiempo: Long = 0L
)