package com.tecsup.pokemon.ui.comparar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tecsup.pokemon.R
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.components.ArtePokemon
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.components.Icono
import com.tecsup.pokemon.ui.components.MensajeCentrado
import com.tecsup.pokemon.ui.components.VidrioBoton
import com.tecsup.pokemon.ui.detalle.nombreTipo
import java.text.NumberFormat
import kotlin.math.abs

@Composable
fun CompararScreen(
    estadoA: UiState<Item>,
    estadoB: UiState<Item>,
    reintentar: () -> Unit,
    elegirOtros: () -> Unit,
) {
    val fallo = listOf(estadoA, estadoB).filterIsInstance<UiState.Error>().firstOrNull()
    when {
        fallo != null -> MensajeCentrado("Faltan datos para comparar", fallo.mensaje, reintentar = reintentar)
        estadoA is UiState.Exito && estadoB is UiState.Exito -> {
            Comparacion(estadoA.datos, estadoB.datos, reintentar, elegirOtros)
        }
        else -> Cargando()
    }
}

@Composable
private fun Comparacion(a: Item, b: Item, reintentar: () -> Unit, elegirOtros: () -> Unit) {
    val colores = MaterialTheme.colorScheme
    var invertido by rememberSaveable { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val movimiento by animateFloatAsState(
        if (invertido) 1f else 0f, spring(.87f, 320f), label = "Intercambio de Pokémon",
    )
    val izquierda = if (invertido) b else a
    val derecha = if (invertido) a else b

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier.widthIn(max = 670.dp).fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp).padding(top = 8.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(17.dp),
        ) {
            BoxWithConstraints(Modifier.fillMaxWidth().aspectRatio(1.58f).clip(RoundedCornerShape(24.dp))) {
                Image(painterResource(R.drawable.prado), null, Modifier.matchParentSize(), contentScale = ContentScale.Crop)
                val oscuro = colores.background.luminance() < .3f
                Box(Modifier.matchParentSize().background(
                    Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) .38f else .04f), Color.Transparent)),
                ))
                val recorrido = with(LocalDensity.current) { (maxWidth * .52f).toPx() }
                val paso = movimiento.coerceIn(0f, 1f)
                val escala = .94f + abs(paso - .5f) * .12f
                ArtePokemon(
                    a, Modifier.align(Alignment.CenterStart).fillMaxWidth(.48f).fillMaxHeight(.80f)
                        .graphicsLayer {
                            translationX = recorrido * paso
                            scaleX = escala
                            scaleY = escala
                        },
                    descripcion = a.titulo,
                )
                ArtePokemon(
                    b, Modifier.align(Alignment.CenterStart).fillMaxWidth(.48f).fillMaxHeight(.80f)
                        .graphicsLayer {
                            translationX = recorrido * (1f - paso)
                            scaleX = escala
                            scaleY = escala
                        },
                    descripcion = b.titulo,
                )
                VidrioBoton(
                    { invertido = !invertido }, Modifier.align(Alignment.BottomCenter).padding(bottom = 9.dp).size(48.dp),
                ) { Icono(R.drawable.poke_comparar, "Intercambiar Pokémon") }
            }
            AnimatedContent(targetState = invertido, label = "Posiciones de las fichas") { lado ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Identidad(if (lado) b else a, Modifier.weight(1f))
                    Identidad(if (lado) a else b, Modifier.weight(1f))
                }
            }
            HorizontalDivider(color = colores.outlineVariant)
            MedidaComparada("Altura", "m", izquierda.alturaDm, derecha.alturaDm, visible)
            MedidaComparada("Peso", "kg", izquierda.pesoHg, derecha.pesoHg, visible)
            if (listOf(a.alturaDm, a.pesoHg, b.alturaDm, b.pesoHg).any { it == null }) {
                Text("Falta descargar una ficha completa.", color = colores.onSurfaceVariant)
                TextButton(onClick = reintentar) { Text("Reintentar") }
            }
            HorizontalDivider(color = colores.outlineVariant)
            TextButton(onClick = elegirOtros, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Elegir otros Pokémon")
            }
        }
    }
}

@Composable
private fun Identidad(item: Item, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text("#" + item.numero.toString().padStart(3, '0'), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(item.titulo, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        Text(
            item.tipos.joinToString(" · ", transform = ::nombreTipo).ifEmpty { "Tipos sin descargar" },
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun MedidaComparada(titulo: String, unidad: String, a: Int?, b: Int?, visible: Boolean) {
    val colores = MaterialTheme.colorScheme
    val maximo = maxOf(a ?: 0, b ?: 0, 1)
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(titulo, style = MaterialTheme.typography.titleMedium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Medida(a, maximo, unidad, colores.primary, visible, Modifier.weight(1f))
            Medida(b, maximo, unidad, colores.tertiary, visible, Modifier.weight(1f))
        }
    }
}

@Composable
private fun Medida(valor: Int?, maximo: Int, unidad: String, tinta: Color, visible: Boolean, modifier: Modifier) {
    val numeros = remember { NumberFormat.getNumberInstance() }
    val avance by animateFloatAsState(
        if (visible && valor != null) valor.toFloat() / maximo else 0f,
        spring(.87f, 270f), label = "Proporción de la medida",
    )
    Column(modifier, verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(
            valor?.let { numeros.format(it / 10.0) + " " + unidad } ?: "—",
            style = MaterialTheme.typography.titleLarge, color = tinta,
        )
        Box(Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(6.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
            Box(Modifier.fillMaxWidth(avance.coerceIn(0f, 1f)).fillMaxHeight().background(tinta, RoundedCornerShape(6.dp)))
        }
    }
}
