package com.example.encuentra_uca.ui.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.encuentra_uca.data.local.TokenManager
import kotlinx.coroutines.flow.first

@Composable
fun SplashScreen(
    tokenManager: TokenManager,
    onHasSession: () -> Unit,
    onNoSession: () -> Unit
) {
    LaunchedEffect(Unit) {
        val token = tokenManager.tokenFlow.first()
        if (!token.isNullOrBlank()) {
            onHasSession()
        } else {
            onNoSession()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}