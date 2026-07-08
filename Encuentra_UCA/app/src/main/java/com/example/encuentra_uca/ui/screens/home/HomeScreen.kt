package com.example.encuentra_uca.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.encuentra_uca.R
import com.example.encuentra_uca.data.remote.dto.ObjetoDto
import com.example.encuentra_uca.ui.FabricaViewModelApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicio(
    fabricaViewModel: FabricaViewModelApp,
    alHacerClicEnObjeto: (Int) -> Unit,
    alHacerClicEnPublicar: () -> Unit,
    alHacerClicEnPerfil: () -> Unit
) {
    val viewModel: ViewModelInicio = viewModel(factory = fabricaViewModel)
    val estadoUi by viewModel.estadoUi.collectAsState()

    val duenoCicloVida = LocalLifecycleOwner.current
    DisposableEffect(duenoCicloVida) {
        val observador = LifecycleEventObserver { _, evento ->
            if (evento == Lifecycle.Event.ON_RESUME) {
                viewModel.actualizar()
            }
        }
        duenoCicloVida.lifecycle.addObserver(observador)
        onDispose {
            duenoCicloVida.lifecycle.removeObserver(observador)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = alHacerClicEnPerfil) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = stringResource(R.string.menu_profile)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = alHacerClicEnPublicar) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.btn_publish))
            }
        }
    ) { valoresRelleno ->

        Column(modifier = Modifier.padding(valoresRelleno)) {

            TabRow(selectedTabIndex = if (estadoUi.tipoSeleccionado == "found") 0 else 1) {
                Tab(
                    selected = estadoUi.tipoSeleccionado == "found",
                    onClick = { viewModel.alSeleccionarTipo("found") },
                    text = { Text(stringResource(R.string.tab_found)) }
                )
                Tab(
                    selected = estadoUi.tipoSeleccionado == "lost",
                    onClick = { viewModel.alSeleccionarTipo("lost") },
                    text = { Text(stringResource(R.string.tab_lost)) }
                )
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(CATEGORIAS) { categoria ->
                    val estaSeleccionada = when {
                        categoria == "Todos" && estadoUi.categoriaSeleccionada == null -> true
                        categoria == estadoUi.categoriaSeleccionada -> true
                        else -> false
                    }
                    FilterChip(
                        selected = estaSeleccionada,
                        onClick = { viewModel.alSeleccionarCategoria(categoria) },
                        label = { Text(categoria) }
                    )
                }
            }

            when {
                estadoUi.estaCargando -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                estadoUi.mensajeError != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = estadoUi.mensajeError ?: "",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                estadoUi.objetos.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.empty_items))
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(estadoUi.objetos) { objeto ->
                            TarjetaObjeto(objeto = objeto, alHacerClic = { alHacerClicEnObjeto(objeto.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaObjeto(objeto: ObjetoDto, alHacerClic: () -> Unit) {
    val categoriaTecnica = objeto.categoria.lowercase()
    
    val (categoriaEnEspañol, emojiCategoria) = when (categoriaTecnica) {
        "electronics" -> "Electrónicos" to "💻"
        "documents" -> "Documentos" to "📄"
        "keys" -> "Llaves" to "🔑"
        "backpacks" -> "Mochilas" to "🎒"
        "clothing" -> "Ropa" to "👕"
        "others" -> "Otros" to "📦"
        else -> objeto.categoria to "📦"
    }

    Card(
        onClick = alHacerClic,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emojiCategoria,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = objeto.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (objeto.tipo == "found") stringResource(R.string.status_found) 
                               else stringResource(R.string.status_searching),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (objeto.tipo == "found") MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    )
                }
                Text(
                    text = categoriaEnEspañol,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = objeto.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "📍 ${objeto.ubicacion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
