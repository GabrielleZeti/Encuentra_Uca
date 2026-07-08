package com.example.encuentra_uca.ui.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.encuentra_uca.data.local.GestorToken
import kotlinx.coroutines.flow.first

@Composable
fun PantallaCarga(
    gestorToken: GestorToken,
    alTenerSesion: () -> Unit,
    alNoTenerSesion: () -> Unit
) {
    LaunchedEffect(Unit) {
        val token = gestorToken.flujoToken.first()
        if (!token.isNullOrBlank()) {
            alTenerSesion()
        } else {
            alNoTenerSesion()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}