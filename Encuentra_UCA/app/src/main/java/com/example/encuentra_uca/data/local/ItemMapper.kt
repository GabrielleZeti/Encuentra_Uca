package com.example.encuentra_uca.data.local

import com.example.encuentra_uca.data.remote.dto.ObjetoDto

fun ObjetoDto.aEntidad(): EntidadObjeto = EntidadObjeto(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    categoria = categoria,
    urlImagen = urlImagen,
    ubicacion = ubicacion,
    encontradoPorId = encontradoPorId,
    encontradoPorEmail = encontradoPorEmail,
    estado = estado,
    tipo = tipo,
    marcaTiempo = marcaTiempo
)

fun EntidadObjeto.aDto(): ObjetoDto = ObjetoDto(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    categoria = categoria,
    urlImagen = urlImagen,
    ubicacion = ubicacion,
    encontradoPorId = encontradoPorId,
    encontradoPorEmail = encontradoPorEmail,
    estado = estado,
    tipo = tipo,
    marcaTiempo = marcaTiempo
)