package com.tecsup.pokemon.ui.components

import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.tecsup.pokemon.R
import com.tecsup.pokemon.domain.model.Item

@Composable
fun ArtePokemon(item: Item, modifier: Modifier = Modifier, descripcion: String? = null) {
    val local = when (item.numero) {
        1 -> R.drawable.pokemon_1
        2 -> R.drawable.pokemon_2
        3 -> R.drawable.pokemon_3
        43 -> R.drawable.pokemon_43
        44 -> R.drawable.pokemon_44
        45 -> R.drawable.pokemon_45
        182 -> R.drawable.pokemon_182
        549 -> R.drawable.pokemon_549
        763 -> R.drawable.pokemon_763
        else -> null
    }
    if (local != null) {
        Image(painterResource(local), descripcion, modifier, contentScale = ContentScale.Fit)
    } else {
        AsyncImage(
            model = item.imagenUrl,
            contentDescription = descripcion,
            modifier = modifier,
            placeholder = painterResource(R.drawable.ic_leaf),
            error = painterResource(R.drawable.ic_leaf),
            fallback = painterResource(R.drawable.ic_leaf),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
fun Corazon(item: Item, pulsar: () -> Unit, modifier: Modifier = Modifier) {
    val colores = MaterialTheme.colorScheme
    VidrioBoton(pulsar, modifier, activo = item.esFavorito) {
        Icono(
            if (item.esFavorito) R.drawable.poke_corazon_lleno else R.drawable.poke_corazon,
            if (item.esFavorito) "Quitar ${item.titulo} de favoritos" else "Guardar ${item.titulo} en favoritos",
            tinte = colores.tertiary,
        )
    }
}
