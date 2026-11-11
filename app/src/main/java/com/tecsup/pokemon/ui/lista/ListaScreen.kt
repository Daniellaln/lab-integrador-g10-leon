package com.tecsup.pokemon.ui.lista

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.components.MensajeCentrado

@Composable
fun ListaScreen(
    estado: UiState<List<Item>>,
    modifier: Modifier = Modifier,
    aviso: String? = null,
    vacio: String = "Sin Pokémon",
    mensajeVacio: String = "Sincroniza para descargar el catálogo.",
    reintentar: (() -> Unit)? = null,
    abrir: ((String) -> Unit)? = null,
    alternar: ((String) -> Unit)? = null,
) {
    Column(modifier.fillMaxSize()) {
        if (aviso != null) {
            Surface(color = MaterialTheme.colorScheme.secondaryContainer) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(aviso, Modifier.weight(1f))
                    if (reintentar != null) TextButton(onClick = reintentar) { Text("Reintentar") }
                }
            }
        }
        when (estado) {
            UiState.Cargando -> Cargando()
            is UiState.Error -> MensajeCentrado("Error al cargar", estado.mensaje, reintentar = reintentar)
            is UiState.Exito -> {
                if (estado.datos.isEmpty()) {
                    MensajeCentrado(vacio, mensajeVacio, reintentar = reintentar)
                } else {
                    LazyColumn(contentPadding = PaddingValues(bottom = 8.dp)) {
                        items(estado.datos, key = Item::id) { pokemon ->
                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                AsyncImage(
                                    model = pokemon.imagenUrl,
                                    contentDescription = null,
                                    modifier = Modifier.size(52.dp),
                                    contentScale = ContentScale.Fit,
                                )
                                TextButton(
                                    onClick = { abrir?.invoke(pokemon.id) },
                                    enabled = abrir != null,
                                    modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                                ) {
                                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                                        Text(pokemon.titulo, style = MaterialTheme.typography.titleMedium)
                                        Text("#${pokemon.numero}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                if (alternar != null) {
                                    TextButton(onClick = { alternar(pokemon.id) }, modifier = Modifier.heightIn(min = 48.dp)) {
                                        Text(if (pokemon.esFavorito) "Quitar" else "Guardar")
                                    }
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}
