package com.example.encuentra_uca.ui.screens.publish

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.encuentra_uca.R
import com.example.encuentra_uca.ui.FabricaViewModelApp
import com.example.encuentra_uca.ui.screens.home.CATEGORIAS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPublicar(
    fabricaViewModel: FabricaViewModelApp,
    alPublicarConExito: () -> Unit,
    alRegresar: () -> Unit
) {
    val viewModel: ViewModelPublicar = viewModel(factory = fabricaViewModel)
    val estadoUi by viewModel.estadoUi.collectAsState()
    var menuDesplegableExpandido by remember { mutableStateOf(false) }

    val categoriasParaPublicar = CATEGORIAS.filter { it != "Todos" }

    LaunchedEffect(estadoUi.publicacionExitosa) {
        if (estadoUi.publicacionExitosa) {
            alPublicarConExito()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.publish_title)) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(valoresRelleno)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = estadoUi.titulo,
                onValueChange = {
                    if (it.length <= 60) viewModel.alCambiarTitulo(it)
                },
                label = { Text(stringResource(R.string.label_object_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            // Selector de tipo
            Text(
                text = stringResource(R.string.label_publish_type),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = estadoUi.tipo == "found",
                    onClick = { viewModel.alCambiarTipo("found") },
                    label = { Text(stringResource(R.string.chip_found)) },
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = estadoUi.tipo == "lost",
                    onClick = { viewModel.alCambiarTipo("lost") },
                    label = { Text(stringResource(R.string.chip_lost)) },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = estadoUi.descripcion,
                onValueChange = {
                    if (it.length <= 200) viewModel.alCambiarDescripcion(it)
                },
                label = { Text(stringResource(R.string.label_detailed_description)) },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = menuDesplegableExpandido,
                onExpandedChange = { menuDesplegableExpandido = it }
            ) {
                OutlinedTextField(
                    value = estadoUi.categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.label_category)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = menuDesplegableExpandido
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )

                ExposedDropdownMenu(
                    expanded = menuDesplegableExpandido,
                    onDismissRequest = { menuDesplegableExpandido = false }
                ) {
                    categoriasParaPublicar.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria) },
                            onClick = {
                                viewModel.alCambiarCategoria(categoria)
                                menuDesplegableExpandido = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = estadoUi.ubicacion,
                onValueChange = {
                    if (it.length <= 80) viewModel.alCambiarUbicacion(it)
                },
                label = { Text(stringResource(R.string.label_place)) },
                modifier = Modifier.fillMaxWidth()
            )

            if (estadoUi.mensajeError != null) {
                Text(
                    text = estadoUi.mensajeError ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = { viewModel.publicar() },
                enabled = !estadoUi.estaCargando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (estadoUi.estaCargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(stringResource(R.string.btn_publish_action))
                }
            }
        }
    }
}