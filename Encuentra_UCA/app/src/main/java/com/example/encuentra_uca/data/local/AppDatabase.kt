package com.example.encuentra_uca.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [EntidadObjeto::class],
    version = 4, // Incrementado a 4 para limpiar todos los datos locales previos
    exportSchema = false
)
abstract class BaseDeDatosApp : RoomDatabase() {
    abstract fun daoObjeto(): DaoObjeto

    companion object {
        @Volatile
        private var INSTANCIA: BaseDeDatosApp? = null

        fun obtenerInstancia(contexto: Context): BaseDeDatosApp {
            return INSTANCIA ?: synchronized(this) {
                Room.databaseBuilder(
                    contexto.applicationContext,
                    BaseDeDatosApp::class.java,
                    "encuentra_uca_db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build().also { INSTANCIA = it }
            }
        }
    }
}
