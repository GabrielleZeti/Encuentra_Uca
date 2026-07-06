package com.example.encuentra_uca.ui.screens.register

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.encuentra_uca.ui.AppViewModelFactory

@Composable
fun RegisterScreen(
    viewModelFactory: AppViewModelFactory,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val viewModel: RegisterViewModel = viewModel(factory = viewModelFactory)
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isRegisterSuccessful) {
        if (uiState.isRegisterSuccessful) {
            onRegisterSuccess()
        }
    }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Usa tu correo institucional UCA",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        OutlinedTextField(
            value = uiState.name,
            onValueChange = { if (it.length <= 50) viewModel.onNameChange(it) },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.email,
            onValueChange = { if (it.length <= 50) viewModel.onEmailChange(it) },
            label = { Text("Correo institucional") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { if (it.length <= 30) viewModel.onPasswordChange(it) },
            label = { Text("Contraseña") },
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar contraseña"
                        else "Mostrar contraseña"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        OutlinedTextField(
            value = uiState.confirmPassword,
            onValueChange = { if (it.length <= 30) viewModel.onConfirmPasswordChange(it) },
            label = { Text("Confirmar contraseña") },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = if (confirmPasswordVisible) "Ocultar contraseña"
                        else "Mostrar contraseña"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = { viewModel.register() },
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Registrarse")
            }
        }

        TextButton(onClick = onNavigateToLogin) {
            Text("¿Ya tienes cuenta? Inicia sesion")
        }
    }
}