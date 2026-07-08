package com.example.encuentra_uca.ui.screens.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.encuentra_uca.R
import com.example.encuentra_uca.ui.FabricaViewModelApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalle(
    idObjeto: Int,
    fabricaViewModel: FabricaViewModelApp,
    alRegresar: () -> Unit
) {
    val viewModel: ViewModelDetalle = viewModel(factory = fabricaViewModel)
    val estadoUi by viewModel.estadoUi.collectAsState()
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    val contexto = LocalContext.current

    LaunchedEffect(idObjeto) {
        viewModel.cargarObjeto(idObjeto)
    }

    LaunchedEffect(estadoUi.estaEliminado) {
        if (estadoUi.estaEliminado) alRegresar()
    }

    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text(stringResource(R.string.delete_dialog_title)) },
            text = { Text(stringResource(R.string.delete_dialog_text)) },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogoEliminar = false
                    viewModel.eliminarObjeto()
                }) {
                    Text(stringResource(R.string.action_confirm), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detail_title)) },
                navigationIcon = {
                    IconButton(onClick = alRegresar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.btn_back)
                        )
                    }
                }
            )
        }
    ) { valoresRelleno ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(valoresRelleno)
        ) {
            when {
                estadoUi.estaCargando || estadoUi.estaEliminando -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                estadoUi.mensajeError != null -> {
                    Text(
                        text = estadoUi.mensajeError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                estadoUi.objeto != null -> {
                    val objeto = estadoUi.objeto!!
                    
                    // Mapeo de categoría para mostrar en español
                    val categoriaEnEspañol = when (objeto.categoria.lowercase()) {
                        "electronics" -> "💻 Electrónicos"
                        "documents" -> "📄 Documentos"
                        "keys" -> "🔑 Llaves"
                        "backpacks" -> "🎒 Mochilas"
                        "clothing" -> "👕 Ropa"
                        "others" -> "📦 Otros"
                        else -> objeto.categoria
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = objeto.titulo,
                            style = MaterialTheme.typography.headlineMedium
                        )

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = categoriaEnEspañol,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "•",
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = if (objeto.tipo == "found") stringResource(R.string.status_found) 
                                           else stringResource(R.string.status_searching),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }

                        FilaInformacion(etiqueta = stringResource(R.string.label_description), valor = objeto.descripcion)
                        FilaInformacion(etiqueta = stringResource(R.string.label_location), valor = objeto.ubicacion)
                        FilaInformacion(etiqueta = stringResource(R.string.label_reported_by), valor = objeto.encontradoPorEmail)

                        // Botón contactar — solo si NO eres el dueño
                        if (!estadoUi.esPropietario) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:${objeto.encontradoPorEmail}")
                                        putExtra(Intent.EXTRA_SUBJECT, "Consulta sobre: ${objeto.titulo}")
                                        putExtra(Intent.EXTRA_TEXT, "Hola, te contacto por el objeto '${objeto.titulo}' publicado en Encuentra UCA...")
                                    }
                                    contexto.startActivity(Intent.createChooser(intent, "Contactar vía correo"))
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0078D4) // Azul Outlook
                                ),
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                            ) {
                                Text("Contactar al publicador", color = Color.White)
                            }
                        }

                        // Botón eliminar — solo si eres el dueño
                        if (estadoUi.esPropietario) {
                            Button(
                                onClick = { mostrarDialogoEliminar = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                ),
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                            ) {
                                Text("Borrar publicación")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilaInformacion(etiqueta: String, valor: String) {
    Column {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
