package com.example.encuentra_uca.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "objetos")
data class EntidadObjeto(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "title") val titulo: String,
    @ColumnInfo(name = "description") val descripcion: String,
    @ColumnInfo(name = "category") val categoria: String,
    @ColumnInfo(name = "imageUrl") val urlImagen: String,
    @ColumnInfo(name = "location") val ubicacion: String,
    @ColumnInfo(name = "foundById") val encontradoPorId: Int,
    @ColumnInfo(name = "foundByEmail") val encontradoPorEmail: String,
    @ColumnInfo(name = "status") val estado: String,
    @ColumnInfo(name = "type") val tipo: String = "found",
    @ColumnInfo(name = "timestamp") val marcaTiempo: Long
)