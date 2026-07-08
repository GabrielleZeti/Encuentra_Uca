package com.example.encuentra_uca.data.repository

import com.example.encuentra_uca.data.local.DaoObjeto
import com.example.encuentra_uca.data.local.aDto
import com.example.encuentra_uca.data.local.aEntidad
import com.example.encuentra_uca.data.remote.ServicioApiObjetos
import com.example.encuentra_uca.data.remote.dto.SolicitudCrearObjeto
import com.example.encuentra_uca.data.remote.dto.ObjetoDto
import kotlinx.coroutines.flow.first

class ItemRepository(
    private val servicioApi: ServicioApiObjetos,
    private val daoObjeto: DaoObjeto
) {
    suspend fun obtenerObjetos(categoria: String? = null, tipo: String = "found"): Result<List<ObjetoDto>> {
        return try {
            val objetosRemotos = servicioApi.obtenerObjetos(categoria, tipo)
            if (categoria == null) {
                daoObjeto.eliminarTodo()
                daoObjeto.insertarTodos(objetosRemotos.map { it.aEntidad() })
            }
            Result.success(objetosRemotos)
        } catch (e: Exception) {
            try {
                val objetosCacheados = if (categoria != null) {
                    daoObjeto.obtenerObjetosPorCategoria(categoria).first()
                } else {
                    daoObjeto.obtenerTodosLosObjetos().first()
                }
                Result.success(objetosCacheados.map { it.aDto() })
            } catch (errorCache: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun obtenerObjetoPorId(id: Int): Result<ObjetoDto> {
        return try {
            Result.success(servicioApi.obtenerObjetoPorId(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearObjeto(token: String, solicitud: SolicitudCrearObjeto): Result<ObjetoDto> {
        return try {
            val nuevoObjeto = servicioApi.crearObjeto(token, solicitud)
            daoObjeto.insertarTodos(listOf(nuevoObjeto.aEntidad()))
            Result.success(nuevoObjeto)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarObjeto(token: String, id: Int): Result<Unit> {
        return try {
            servicioApi.eliminarObjeto(token, id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}