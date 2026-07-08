package com.example.encuentra_uca.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.encuentra_uca.ui.FabricaViewModelApp
import com.example.encuentra_uca.ui.screens.detail.PantallaDetalle
import com.example.encuentra_uca.ui.screens.home.PantallaInicio
import com.example.encuentra_uca.ui.screens.login.PantallaInicioSesion
import com.example.encuentra_uca.ui.screens.profile.PantallaPerfil
import com.example.encuentra_uca.ui.screens.publish.PantallaPublicar
import com.example.encuentra_uca.ui.screens.register.PantallaRegistro
import com.example.encuentra_uca.ui.screens.splash.PantallaCarga
import com.example.encuentra_uca.data.local.GestorToken

sealed class Pantalla(val ruta: String) {
    object Carga : Pantalla("carga")
    object InicioSesion : Pantalla("inicio_sesion")
    object Registro : Pantalla("registro")
    object Inicio : Pantalla("inicio")
    object Detalle : Pantalla("detalle/{idObjeto}") {
        fun crearRuta(idObjeto: Int) = "detalle/$idObjeto"
    }
    object Publicar : Pantalla("publicar")
    object Perfil : Pantalla("perfil")
}

@Composable
fun GrafoNavegacionApp(fabricaViewModel: FabricaViewModelApp) {
    val controladorNavegacion = rememberNavController()

    NavHost(
        navController = controladorNavegacion,
        startDestination = Pantalla.Carga.ruta
    ) {
        composable(Pantalla.Carga.ruta) {
            PantallaCarga(
                gestorToken = GestorToken(androidx.compose.ui.platform.LocalContext.current),
                alTenerSesion = {
                    controladorNavegacion.navigate(Pantalla.Inicio.ruta) {
                        popUpTo(Pantalla.Carga.ruta) { inclusive = true }
                    }
                },
                alNoTenerSesion = {
                    controladorNavegacion.navigate(Pantalla.InicioSesion.ruta) {
                        popUpTo(Pantalla.Carga.ruta) { inclusive = true }
                    }
                }
            )
        }
        composable(Pantalla.InicioSesion.ruta) {
            PantallaInicioSesion(
                fabricaViewModel = fabricaViewModel,
                alIniciarSesionConExito = {
                    controladorNavegacion.navigate(Pantalla.Inicio.ruta) {
                        popUpTo(Pantalla.InicioSesion.ruta) { inclusive = true }
                    }
                },
                alNavegarARegistro = {
                    controladorNavegacion.navigate(Pantalla.Registro.ruta)
                }
            )
        }
        composable(Pantalla.Registro.ruta) {
            PantallaRegistro(
                fabricaViewModel = fabricaViewModel,
                alRegistrarseConExito = {
                    controladorNavegacion.navigate(Pantalla.Inicio.ruta) {
                        popUpTo(Pantalla.Registro.ruta) { inclusive = true }
                    }
                },
                alNavegarAInicioSesion = {
                    controladorNavegacion.popBackStack()
                }
            )
        }
        composable(Pantalla.Inicio.ruta) {
            PantallaInicio(
                fabricaViewModel = fabricaViewModel,
                alHacerClicEnObjeto = { idObjeto ->
                    controladorNavegacion.navigate(Pantalla.Detalle.crearRuta(idObjeto))
                },
                alHacerClicEnPublicar = {
                    controladorNavegacion.navigate(Pantalla.Publicar.ruta)
                },
                alHacerClicEnPerfil = {
                    controladorNavegacion.navigate(Pantalla.Perfil.ruta)
                }
            )
        }
        composable(Pantalla.Detalle.ruta) { entradaPilaBack ->
            val idObjeto = entradaPilaBack.arguments?.getString("idObjeto")?.toIntOrNull() ?: return@composable
            PantallaDetalle(
                idObjeto = idObjeto,
                fabricaViewModel = fabricaViewModel,
                alRegresar = { controladorNavegacion.popBackStack() }
            )
        }
        composable(Pantalla.Publicar.ruta) {
            PantallaPublicar(
                fabricaViewModel = fabricaViewModel,
                alPublicarConExito = {
                    controladorNavegacion.navigate(Pantalla.Inicio.ruta) {
                        popUpTo(Pantalla.Inicio.ruta) { inclusive = true }
                    }
                },
                alRegresar = { controladorNavegacion.popBackStack() }
            )
        }
        composable(Pantalla.Perfil.ruta) {
            PantallaPerfil(
                fabricaViewModel = fabricaViewModel,
                alCerrarSesion = {
                    controladorNavegacion.navigate(Pantalla.InicioSesion.ruta) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                alRegresar = { controladorNavegacion.popBackStack() }
            )
        }
    }
}