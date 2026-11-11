package com.tecsup.pokemon.ui.comparar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tecsup.pokemon.R
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.domain.model.EstadisticasPokemon
import com.tecsup.pokemon.domain.model.nombreLegible
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Comparacion(a: Item, b: Item, reintentar: () -> Unit, elegirOtros: () -> Unit) {
    val colores = MaterialTheme.colorScheme
    var invertido by rememberSaveable { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }
    var situacion by rememberSaveable { mutableStateOf(Situacion.INICIATIVA) }
    var elegirSituacion by rememberSaveable { mutableStateOf(false) }
    var mostrarTodo by rememberSaveable { mutableStateOf(false) }
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
                Image(painterResource(R.drawable.arena_duelo), null, Modifier.matchParentSize(), contentScale = ContentScale.Crop)
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
                    { invertido = !invertido }, Modifier.align(Alignment.BottomCenter).padding(bottom = 9.dp).size(56.dp),
                ) { Icono(R.drawable.poke_comparar, "Intercambiar Pokémon") }
            }
            AnimatedContent(targetState = invertido, label = "Posiciones de las fichas") { lado ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Identidad(if (lado) b else a, Modifier.weight(1f))
                    Identidad(if (lado) a else b, Modifier.weight(1f))
                }
            }
            VidrioBoton(
                { elegirSituacion = true }, Modifier.fillMaxWidth().heightIn(min = 60.dp),
                forma = RoundedCornerShape(24.dp), destacado = true,
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(situacion.titulo, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, color = colores.onPrimary)
                    Text("Cambiar", color = colores.onPrimary, style = MaterialTheme.typography.labelMedium)
                }
            }
            val izq = situacion.valor(izquierda, derecha)
            val der = situacion.valor(derecha, izquierda)
            val ganador = when {
                izq == null || der == null -> "Datos pendientes"
                izq == der -> "Empate"
                izq > der -> "Ventaja de ${izquierda.titulo}"
                else -> "Ventaja de ${derecha.titulo}"
            }
            AnimatedContent(ganador, label = "Ventaja según la situación") {
                Text(it, style = MaterialTheme.typography.headlineSmall, color = colores.onBackground)
            }
            Text(situacion.criterio, color = colores.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            MedidaComparada(situacion.titulo, if (situacion == Situacion.DUELO) "×" else "", izq, der, visible)
            if (izq == null || der == null) TextButton(onClick = reintentar) { Text("Descargar datos faltantes") }
            if (situacion == Situacion.DUELO) {
                Text("Efectividad por tipo. En combate también influyen los movimientos y las habilidades.",
                    style = MaterialTheme.typography.bodySmall, color = colores.onSurfaceVariant)
            }
            TextButton(
                onClick = { mostrarTodo = !mostrarTodo },
                modifier = Modifier.align(Alignment.CenterHorizontally).heightIn(min = 48.dp),
            ) { Text(if (mostrarTodo) "Ocultar atributos" else "Ver todos los atributos") }
            AnimatedVisibility(mostrarTodo) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    EstadisticasPokemon.forEach { (id, etiqueta) ->
                        MedidaComparada(etiqueta, "",
                            izquierda.perfil?.estadisticas?.get(id)?.toFloat(),
                            derecha.perfil?.estadisticas?.get(id)?.toFloat(), visible)
                    }
                    MedidaComparada("Altura", "m", izquierda.alturaDm?.div(10f), derecha.alturaDm?.div(10f), visible)
                    MedidaComparada("Peso", "kg", izquierda.pesoHg?.div(10f), derecha.pesoHg?.div(10f), visible)
                    Text("Habilidades", style = MaterialTheme.typography.titleLarge)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        listOf(izquierda, derecha).forEach { pokemon ->
                            Text(
                                pokemon.perfil?.habilidades.orEmpty().joinToString("\n") {
                                    it.id.nombreLegible() + if (it.oculta) " · oculta" else ""
                                }.ifEmpty { "Pendientes de descarga" },
                                modifier = Modifier.weight(1f), color = colores.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = colores.outlineVariant)
            TextButton(onClick = elegirOtros, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Elegir otros Pokémon")
            }
        }
    }
    if (elegirSituacion) {
        ModalBottomSheet(onDismissRequest = { elegirSituacion = false }) {
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 22.dp).padding(bottom = 18.dp)) {
                Text("¿Qué quieres comparar?", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                Situacion.entries.forEach { opcion ->
                    Row(
                        Modifier.fillMaxWidth().heightIn(min = 58.dp).clickable(role = Role.Button) {
                            situacion = opcion
                            elegirSituacion = false
                        },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f).padding(vertical = 7.dp)) {
                            Text(opcion.titulo, style = MaterialTheme.typography.titleMedium)
                            Text(opcion.criterio, color = colores.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                        }
                        if (opcion == situacion) Text("✓", color = colores.primary)
                    }
                    HorizontalDivider(color = colores.outlineVariant)
                }
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
private fun MedidaComparada(titulo: String, unidad: String, a: Float?, b: Float?, visible: Boolean) {
    val colores = MaterialTheme.colorScheme
    val maximo = maxOf(a ?: 0f, b ?: 0f, 1f)
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(titulo, style = MaterialTheme.typography.titleMedium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Medida(a, maximo, unidad, colores.primary, visible, Modifier.weight(1f))
            Medida(b, maximo, unidad, colores.tertiary, visible, Modifier.weight(1f))
        }
    }
}

@Composable
private fun Medida(valor: Float?, maximo: Float, unidad: String, tinta: Color, visible: Boolean, modifier: Modifier) {
    val numeros = remember { NumberFormat.getNumberInstance() }
    val avance by animateFloatAsState(
        if (visible && valor != null) valor / maximo else 0f,
        spring(.87f, 270f), label = "Proporción de la medida",
    )
    Column(modifier, verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(
            valor?.let { numeros.format(it) + if (unidad.isEmpty()) "" else " $unidad" } ?: "—",
            style = MaterialTheme.typography.titleLarge, color = tinta,
        )
        Box(Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(6.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
            Box(Modifier.fillMaxWidth(avance.coerceIn(0f, 1f)).fillMaxHeight().background(tinta, RoundedCornerShape(6.dp)))
        }
    }
}
