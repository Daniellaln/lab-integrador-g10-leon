package com.tecsup.pokemon.ui.detalle

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tecsup.pokemon.R
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.components.ArtePokemon
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.components.Corazon
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
        is UiState.Exito -> Ficha(estado.datos, reintentar, alternar, modifier)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Ficha(item: Item, reintentar: () -> Unit, alternar: (() -> Unit)?, modifier: Modifier) {
    val colores = MaterialTheme.colorScheme
    val numeros = NumberFormat.getNumberInstance()
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier.widthIn(max = 650.dp).fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp).padding(top = 7.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            Box(Modifier.fillMaxWidth().aspectRatio(1.27f).clip(RoundedCornerShape(20.dp))) {
                Image(painterResource(R.drawable.prado), null, Modifier.matchParentSize(), contentScale = ContentScale.Crop)
                if (colores.background.luminance() < .3f) {
                    Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = .32f)))
                }
                ArtePokemon(
                    item, Modifier.align(Alignment.Center).fillMaxWidth(.70f).fillMaxHeight(.77f),
                    descripcion = item.titulo,
                )
                if (alternar != null) Corazon(item, alternar, Modifier.align(Alignment.TopEnd).padding(11.dp).size(52.dp))
            }

            Column(Modifier.padding(horizontal = 4.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(item.titulo, style = MaterialTheme.typography.displaySmall)
                Text("Pokédex #" + item.numero.toString().padStart(3, '0'), color = colores.onSurfaceVariant)
            }
            val altura = item.alturaDm
            val peso = item.pesoHg
            if (altura == null || peso == null) {
                Text("Conéctate para descargar la ficha completa.", Modifier.padding(horizontal = 4.dp))
                TextButton(onClick = reintentar) { Text("Reintentar") }
            } else {
                FlowRow(
                    Modifier.padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    item.tipos.forEach { tipo ->
                        val planta = tipo == "grass"
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (planta) colores.primaryContainer else colores.tertiaryContainer,
                            contentColor = if (planta) colores.onPrimaryContainer else colores.onTertiaryContainer,
                        ) {
                            Text(nombreTipo(tipo), Modifier.padding(horizontal = 12.dp, vertical = 5.dp), style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
                HorizontalDivider(color = colores.outlineVariant)
                FlowRow(
                    Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(38.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Medida(numeros.format(altura / 10.0) + " m", "Altura")
                    Medida(numeros.format(peso / 10.0) + " kg", "Peso")
                }
                HorizontalDivider(color = colores.outlineVariant)
            }
        }
    }
}

@Composable
private fun Medida(valor: String, etiqueta: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(valor, style = MaterialTheme.typography.headlineMedium)
        Text(etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
