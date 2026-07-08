package com.example.encuentra_uca.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.almacenDatos by preferencesDataStore(name = "encuentra_uca_prefs")

class GestorToken(private val contexto: Context) {
    private val CLAVE_TOKEN = stringPreferencesKey("auth_token")
    private val CLAVE_CORREO_USUARIO = stringPreferencesKey("user_email")
    private val CLAVE_NOMBRE_USUARIO = stringPreferencesKey("user_name")

    val flujoToken: Flow<String?> = contexto.almacenDatos.data.map { prefs ->
        prefs[CLAVE_TOKEN]
    }

    val flujoCorreoUsuario: Flow<String?> = contexto.almacenDatos.data.map { prefs ->
        prefs[CLAVE_CORREO_USUARIO]
    }

    val flujoNombreUsuario: Flow<String?> = contexto.almacenDatos.data.map { prefs ->
        prefs[CLAVE_NOMBRE_USUARIO]
    }

    suspend fun guardarSesion(token: String, correo: String, nombre: String) {
        contexto.almacenDatos.edit { prefs ->
            prefs[CLAVE_TOKEN] = token
            prefs[CLAVE_CORREO_USUARIO] = correo
            prefs[CLAVE_NOMBRE_USUARIO] = nombre
        }
    }

    suspend fun cerrarSesion() {
        contexto.almacenDatos.edit { prefs ->
            prefs.clear()
        }
    }
}