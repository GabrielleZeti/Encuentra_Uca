package com.example.encuentra_uca.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.encuentra_uca.data.local.BaseDeDatosApp
import com.example.encuentra_uca.data.local.GestorToken
import com.example.encuentra_uca.data.remote.ServicioApiAutenticacion
import com.example.encuentra_uca.data.remote.ServicioApiObjetos
import com.example.encuentra_uca.data.repository.RepositorioAutenticacion
import com.example.encuentra_uca.data.repository.ItemRepository
import com.example.encuentra_uca.ui.screens.detail.ViewModelDetalle
import com.example.encuentra_uca.ui.screens.home.ViewModelInicio
import com.example.encuentra_uca.ui.screens.login.ViewModelInicioSesion
import com.example.encuentra_uca.ui.screens.profile.ViewModelPerfil
import com.example.encuentra_uca.ui.screens.publish.ViewModelPublicar
import com.example.encuentra_uca.ui.screens.register.ViewModelRegistro

class FabricaViewModelApp(private val contexto: Context) : ViewModelProvider.Factory {

    private val servicioApiAutenticacion = ServicioApiAutenticacion()
    private val servicioApiObjetos = ServicioApiObjetos()
    private val gestorToken = GestorToken(contexto)
    private val baseDeDatos = BaseDeDatosApp.obtenerInstancia(contexto)
    private val daoObjeto = baseDeDatos.daoObjeto()
    private val repositorioAutenticacion = RepositorioAutenticacion(servicioApiAutenticacion, gestorToken)
    private val itemRepository = ItemRepository(servicioApiObjetos, daoObjeto)

    override fun <T : ViewModel> create(claseModelo: Class<T>, extras: CreationExtras): T {
        return when {
            claseModelo.isAssignableFrom(ViewModelInicioSesion::class.java) ->
                ViewModelInicioSesion(repositorioAutenticacion) as T
            claseModelo.isAssignableFrom(ViewModelRegistro::class.java) ->
                ViewModelRegistro(repositorioAutenticacion) as T
            claseModelo.isAssignableFrom(ViewModelInicio::class.java) ->
                ViewModelInicio(itemRepository) as T
            claseModelo.isAssignableFrom(ViewModelDetalle::class.java) ->
                ViewModelDetalle(itemRepository, gestorToken) as T
            claseModelo.isAssignableFrom(ViewModelPublicar::class.java) ->
                ViewModelPublicar(itemRepository, gestorToken) as T
            claseModelo.isAssignableFrom(ViewModelPerfil::class.java) ->
                ViewModelPerfil(repositorioAutenticacion) as T
            else ->
                throw IllegalArgumentException("ViewModel desconocido: ${claseModelo.name}")
        }
    }
}