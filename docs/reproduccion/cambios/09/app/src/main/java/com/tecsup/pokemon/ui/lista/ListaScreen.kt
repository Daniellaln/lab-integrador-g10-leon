package com.tecsup.pokemon.ui.lista

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tecsup.pokemon.R
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.components.*

@Composable
fun ListaScreen(
    estado: UiState<List<Item>>,
    modifier: Modifier = Modifier,
    aviso: String? = null,
    vacio: String = "Sin Pokémon",
    mensajeVacio: String = "Sincroniza para descargar el catálogo.",
    reintentar: (() -> Unit)? = null,
    abrir: (String) -> Unit,
    alternar: ((String) -> Unit)? = null,
    explorar: (() -> Unit)? = null,
) {
    val catalogo = explorar == null
    var buscar by rememberSaveable { mutableStateOf(false) }
    var consulta by rememberSaveable { mutableStateOf("") }
    val colores = MaterialTheme.colorScheme
    val foco = LocalFocusManager.current

    Column(modifier.fillMaxSize()) {
        if (aviso != null) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(13.dp)).background(colores.secondaryContainer)
                    .padding(start = 12.dp, end = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icono(R.drawable.poke_red, null, tinte = colores.onSecondaryContainer)
                Text(aviso, Modifier.weight(1f).padding(vertical = 9.dp), color = colores.onSecondaryContainer)
                if (reintentar != null) TextButton(onClick = reintentar) { Text("Reintentar") }
            }
        }
        when (estado) {
            UiState.Cargando -> Cargando()
            is UiState.Error -> MensajeCentrado("Error al cargar", estado.mensaje, reintentar = reintentar)
            is UiState.Exito -> {
                val lista = remember(estado.datos, consulta) {
                    if (catalogo) estado.datos.buscarPokemon(consulta) else estado.datos
                }
                if (!catalogo && lista.isEmpty()) {
                    FavoritosVacios(vacio, mensajeVacio, explorar)
                } else {
                    if (catalogo) {
                        Row(
                            Modifier.fillMaxWidth().padding(start = 22.dp, end = 20.dp, bottom = 7.dp),
                            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val cantidad = when {
                                !buscar || consulta.isBlank() -> "${estado.datos.size} Pokémon de tipo Planta"
                                lista.size == 1 -> "1 resultado"
                                else -> "${lista.size} resultados"
                            }
                            Text(cantidad, Modifier.weight(1f), color = colores.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                            VidrioBoton(
                                {
                                    buscar = !buscar
                                    if (!buscar) {
                                        consulta = ""
                                        foco.clearFocus()
                                    }
                                },
                                Modifier.size(54.dp), activo = buscar,
                            ) {
                                Icono(if (buscar) R.drawable.poke_cerrar else R.drawable.poke_buscar, if (buscar) "Cerrar búsqueda" else "Buscar Pokémon")
                            }
                        }
                        if (buscar) {
                            val solicitarFoco = remember { FocusRequester() }
                            OutlinedTextField(
                                value = consulta, onValueChange = { consulta = it },
                                label = { Text("Nombre o número") },
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 10.dp).focusRequester(solicitarFoco),
                                shape = RoundedCornerShape(16.dp), singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search, autoCorrectEnabled = false),
                                keyboardActions = KeyboardActions(onSearch = { foco.clearFocus() }),
                                trailingIcon = if (consulta.isEmpty()) null else {
                                    { IconButton(onClick = { consulta = "" }) { Icono(R.drawable.poke_cerrar, "Limpiar búsqueda") } }
                                },
                            )
                            LaunchedEffect(Unit) { solicitarFoco.requestFocus() }
                        }
                    }
                    if (lista.isEmpty()) {
                        if (consulta.isNotBlank()) MensajeCentrado("Sin resultados", "Prueba con otro nombre o número.")
                        else MensajeCentrado(vacio, mensajeVacio, reintentar = reintentar)
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(145.dp),
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(lista, key = Item::id) { pokemon ->
                                FichaCatalogo(pokemon, { abrir(pokemon.id) }, alternar?.let { alternarFavorito -> { alternarFavorito(pokemon.id) } })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FichaCatalogo(item: Item, abrir: () -> Unit, alternar: (() -> Unit)?) {
    val colores = MaterialTheme.colorScheme
    val pigmento = when (item.numero % 3) {
        0 -> colores.tertiaryContainer
        1 -> colores.primaryContainer
        else -> colores.secondaryContainer
    }
    val forma = RoundedCornerShape(24.dp)
    Surface(onClick = abrir, shape = forma, color = colores.surface, border = BorderStroke(1.dp, colores.outlineVariant)) {
        Column {
            Box(
                Modifier.fillMaxWidth().aspectRatio(1.13f)
                    .background(Brush.radialGradient(listOf(colores.surface, pigmento))),
            ) {
                ArtePokemon(item, Modifier.fillMaxSize().padding(horizontal = 13.dp, vertical = 12.dp))
                if (alternar != null) Corazon(item, alternar, Modifier.align(Alignment.TopEnd).padding(5.dp).size(48.dp))
            }
            Column(Modifier.fillMaxWidth().heightIn(min = 70.dp).padding(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 11.dp)) {
                Text("#" + item.numero.toString().padStart(3, '0'), style = MaterialTheme.typography.labelMedium, color = colores.onSurfaceVariant)
                Text(item.titulo, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun FavoritosVacios(titulo: String, texto: String, explorar: (() -> Unit)?) {
    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Image(
            painterResource(R.drawable.hab_pradera), null,
            Modifier.widthIn(max = 400.dp).fillMaxWidth().aspectRatio(1.54f).clip(RoundedCornerShape(18.dp)),
            contentScale = ContentScale.Crop,
        )
        Text(titulo, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Text(texto, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (explorar != null) {
            VidrioBoton(explorar, Modifier.heightIn(min = 57.dp), destacado = true) {
                Text("Explorar catálogo", Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
