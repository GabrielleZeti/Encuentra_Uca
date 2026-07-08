package com.example.encuentra_uca.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.encuentra_uca.R
import com.example.encuentra_uca.ui.FabricaViewModelApp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip

@Composable
fun PantallaInicioSesion(
    fabricaViewModel: FabricaViewModelApp,
    alIniciarSesionConExito: () -> Unit,
    alNavegarARegistro: () -> Unit
) {
    val viewModel: ViewModelInicioSesion = viewModel(factory = fabricaViewModel)
    val estadoUi by viewModel.estadoUi.collectAsState()

    LaunchedEffect(estadoUi.inicioSesionExitoso) {
        if (estadoUi.inicioSesionExitoso) {
            alIniciarSesionConExito()
        }
    }

    var contrasenaVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo Encuentra UCA",
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(24.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = stringResource(R.string.login_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        OutlinedTextField(
            value = estadoUi.correo,
            onValueChange = { if (it.length <= 50) viewModel.alCambiarCorreo(it) },
            label = { Text(stringResource(R.string.label_email)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
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

        if (estadoUi.mensajeError != null) {
            Text(
                text = estadoUi.mensajeError ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = { viewModel.iniciarSesion() },
            enabled = !estadoUi.estaCargando,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            if (estadoUi.estaCargando) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text(stringResource(R.string.btn_login))
            }
        }

        TextButton(onClick = alNavegarARegistro) {
            Text(stringResource(R.string.link_register))
        }
    }
}