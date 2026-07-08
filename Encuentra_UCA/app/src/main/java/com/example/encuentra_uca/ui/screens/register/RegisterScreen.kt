package com.example.encuentra_uca.ui.screens.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.encuentra_uca.R
import com.example.encuentra_uca.ui.FabricaViewModelApp

@Composable
fun PantallaRegistro(
    fabricaViewModel: FabricaViewModelApp,
    alRegistrarseConExito: () -> Unit,
    alNavegarAInicioSesion: () -> Unit
) {
    val viewModel: ViewModelRegistro = viewModel(factory = fabricaViewModel)
    val estadoUi by viewModel.estadoUi.collectAsState()

    LaunchedEffect(estadoUi.registroExitoso) {
        if (estadoUi.registroExitoso) {
            alRegistrarseConExito()
        }
    }
    var contrasenaVisible by remember { mutableStateOf(false) }
    var confirmarContrasenaVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.register_title),
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = stringResource(R.string.register_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        OutlinedTextField(
            value = estadoUi.nombre,
            onValueChange = { if (it.length <= 50) viewModel.alCambiarNombre(it) },
            label = { Text(stringResource(R.string.label_full_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = estadoUi.correo,
            onValueChange = { if (it.length <= 50) viewModel.alCambiarCorreo(it) },
            label = { Text(stringResource(R.string.label_email)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        OutlinedTextField(
            value = estadoUi.contrasena,
            onValueChange = { if (it.length <= 30) viewModel.alCambiarContrasena(it) },
            label = { Text(stringResource(R.string.label_password)) },
            visualTransformation = if (contrasenaVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { contrasenaVisible = !contrasenaVisible }) {
                    Icon(
                        imageVector = if (contrasenaVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = if (contrasenaVisible) stringResource(R.string.desc_hide_password)
                        else stringResource(R.string.desc_show_password)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        OutlinedTextField(
            value = estadoUi.confirmarContrasena,
            onValueChange = { if (it.length <= 30) viewModel.alCambiarConfirmarContrasena(it) },
            label = { Text(stringResource(R.string.label_confirm_password)) },
            visualTransformation = if (confirmarContrasenaVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { confirmarContrasenaVisible = !confirmarContrasenaVisible }) {
                    Icon(
                        imageVector = if (confirmarContrasenaVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = if (confirmarContrasenaVisible) stringResource(R.string.desc_hide_password)
                        else stringResource(R.string.desc_show_password)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        if (estadoUi.mensajeError != null) {
            Text(
                text = estadoUi.mensajeError ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = { viewModel.registrar() },
            enabled = !estadoUi.estaCargando,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            if (estadoUi.estaCargando) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text(stringResource(R.string.btn_register))
            }
        }

        TextButton(onClick = alNavegarAInicioSesion) {
            Text(stringResource(R.string.link_login))
        }
    }
}