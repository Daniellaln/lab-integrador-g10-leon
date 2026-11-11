package com.tecsup.pokemon.ui.favoritos

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.lista.ListaScreen

@Composable
fun FavoritosScreen(
    estado: UiState<List<Item>>,
    abrir: (String) -> Unit,
    alternar: (String) -> Unit,
    explorar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListaScreen(
        estado = estado, modifier = modifier,
        vacio = "Aún no hay favoritos",
        mensajeVacio = "Guarda un Pokémon para encontrarlo aquí.",
        abrir = abrir, alternar = alternar, explorar = explorar,
    )
}
