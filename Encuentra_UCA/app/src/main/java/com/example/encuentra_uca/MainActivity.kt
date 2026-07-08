package com.example.encuentra_uca

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.encuentra_uca.ui.FabricaViewModelApp
import com.example.encuentra_uca.ui.navigation.GrafoNavegacionApp
import com.example.encuentra_uca.ui.theme.Encuentra_UCATheme
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(instanciaGuardada: Bundle?) {
        super.onCreate(instanciaGuardada)
        enableEdgeToEdge()

        FirebaseApp.initializeApp(this)

        // Suscripción al tópico de notificaciones
        FirebaseMessaging.getInstance().subscribeToTopic("nuevos_objetos")
            .addOnCompleteListener { tarea ->
                if (tarea.isSuccessful) {
                    Log.d("FCM", "Suscrito exitosamente a nuevos_objetos")
                } else {
                    Log.e("FCM", "Error al suscribirse a FCM: ${tarea.exception?.message}")
                }
            }

        setContent {
            Encuentra_UCATheme {
                GrafoNavegacionApp(
                    fabricaViewModel = FabricaViewModelApp(applicationContext)
                )
            }
        }
    }
}
