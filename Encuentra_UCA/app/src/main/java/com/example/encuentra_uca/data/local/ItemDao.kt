package com.example.encuentra_uca.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoObjeto {

    @Query("SELECT * FROM objetos ORDER BY timestamp DESC")
    fun obtenerTodosLosObjetos(): Flow<List<EntidadObjeto>>

    @Query("SELECT * FROM objetos WHERE category = :categoria ORDER BY timestamp DESC")
    fun obtenerObjetosPorCategoria(categoria: String): Flow<List<EntidadObjeto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(objetos: List<EntidadObjeto>)

    @Query("DELETE FROM objetos")
    suspend fun eliminarTodo()
}