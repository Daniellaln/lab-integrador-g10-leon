package com.tecsup.pokemon.ui.detalle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.components.MensajeCentrado
import java.text.NumberFormat

@Composable
fun DetalleScreen(
    estado: UiState<Item>,
    reintentar: () -> Unit,
    alternar: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    when (estado) {
        UiState.Cargando -> Cargando(modifier)
        is UiState.Error -> MensajeCentrado("Ficha no disponible", estado.mensaje, modifier, reintentar)
        is UiState.Exito -> {
            val pokemon = estado.datos
            val numeros = NumberFormat.getNumberInstance()
            Column(
                modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AsyncImage(pokemon.imagenUrl, pokemon.titulo, Modifier.size(192.dp))
                Text(pokemon.titulo, style = MaterialTheme.typography.headlineMedium)
                Text("Pokédex #${pokemon.numero}")
                val altura = pokemon.alturaDm
                val peso = pokemon.pesoHg
                if (altura != null && peso != null) {
                    Text("Altura: ${numeros.format(altura / 10.0)} m")
                    Text("Peso: ${numeros.format(peso / 10.0)} kg")
                    Text("Tipo: ${pokemon.tipos.joinToString(" · ") { nombreTipo(it) }}")
                } else {
                    Text("Conéctate para descargar la ficha completa.")
                    TextButton(onClick = reintentar) { Text("Reintentar") }
                }
                if (alternar != null) {
                    Button(onClick = alternar) {
                        Text(if (pokemon.esFavorito) "Quitar de favoritos" else "Guardar en favoritos")
                    }
                }
            }
        }
    }
}

private fun nombreTipo(tipo: String) = when (tipo) {
    "grass" -> "Planta"
    "poison" -> "Veneno"
    "ground" -> "Tierra"
    "normal" -> "Normal"
    "bug" -> "Bicho"
    "flying" -> "Volador"
    "psychic" -> "Psíquico"
    "ghost" -> "Fantasma"
    "fairy" -> "Hada"
    "fighting" -> "Lucha"
    "dark" -> "Siniestro"
    "water" -> "Agua"
    "ice" -> "Hielo"
    "fire" -> "Fuego"
    "rock" -> "Roca"
    "steel" -> "Acero"
    "dragon" -> "Dragón"
    "electric" -> "Eléctrico"
    else -> tipo
}
