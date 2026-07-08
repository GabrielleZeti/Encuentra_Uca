package com.example.encuentra_uca.data.remote

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.encuentra_uca.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FcmService : FirebaseMessagingService() {

    override fun onMessageReceived(mensajeRemoto: RemoteMessage) {
        val titulo = mensajeRemoto.notification?.title ?: "Encuentra UCA"
        val cuerpo = mensajeRemoto.notification?.body ?: "Nuevo objeto encontrado"
        mostrarNotificacion(titulo, cuerpo)
    }

    override fun onNewToken(token: String) {
        // Se puede implementar para registrar el token en el servidor si es necesario
    }

    private fun mostrarNotificacion(titulo: String, cuerpo: String) {
        val idCanal = "encuentra_uca_canal"
        val gestorNotificaciones =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val canal = NotificationChannel(
            idCanal,
            "Encuentra UCA",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        gestorNotificaciones.createNotificationChannel(canal)

        val intencion = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val intencionPendiente = PendingIntent.getActivity(
            this, 0, intencion,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(this, idCanal)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setAutoCancel(true)
            .setContentIntent(intencionPendiente)
            .build()

        gestorNotificaciones.notify(System.currentTimeMillis().toInt(), notificacion)
    }
}