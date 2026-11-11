package com.tecsup.pokemon.ui.detalle

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.tecsup.pokemon.R
import com.tecsup.pokemon.domain.model.*
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.components.*
import java.text.NumberFormat

private enum class SeccionFicha(val etiqueta: String) {
    FICHA("Ficha"), TECNICAS("Técnicas"), EVOLUCION("Evolución")
}

@Composable
fun DetalleScreen(
    estado: UiState<Item>,
    reintentar: () -> Unit,
    alternar: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    etiquetaComparar: String = "",
    tecnica: UiState<FichaTecnica>? = null,
    consultarTecnica: ((String, Boolean) -> Unit)? = null,
    cerrarTecnica: (() -> Unit)? = null,
    comparar: (() -> Unit)? = null,
) {
    when (estado) {
        UiState.Cargando -> Cargando(modifier)
        is UiState.Error -> MensajeCentrado("Ficha no disponible", estado.mensaje, modifier, reintentar)
        is UiState.Exito -> Ficha(estado.datos, reintentar, alternar, modifier, etiquetaComparar, consultarTecnica, comparar)
    }
    if (tecnica != null && cerrarTecnica != null) DetalleTecnica(tecnica, cerrarTecnica)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Ficha(
    item: Item,
    reintentar: () -> Unit,
    alternar: (() -> Unit)?,
    modifier: Modifier,
    etiquetaComparar: String,
    consultarTecnica: ((String, Boolean) -> Unit)?,
    comparar: (() -> Unit)?,
) {
    val colores = MaterialTheme.colorScheme
    val perfil = item.perfil
    val (habitat, documentado) = item.habitatVisual()
    var seccion by rememberSaveable(item.id) { mutableStateOf(SeccionFicha.FICHA) }
    val entrada = remember(item.id) { Animatable(0f) }
    LaunchedEffect(item.id) { entrada.animateTo(1f, tween(390)) }

    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Box(Modifier.widthIn(max = 670.dp).fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                Column(
                    Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                        .padding(horizontal = 38.dp).padding(top = 5.dp, bottom = 15.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                ) {
                    Box(Modifier.fillMaxWidth().aspectRatio(1.42f).clip(RoundedCornerShape(28.dp))) {
                        Image(painterResource(habitat.fondo), null, Modifier.matchParentSize(), contentScale = ContentScale.Crop)
                        Box(
                            Modifier.align(Alignment.BottomCenter).padding(bottom = 29.dp)
                                .fillMaxWidth(.44f).height(22.dp)
                                .background(Brush.radialGradient(listOf(Color(0x4D102C23), Color.Transparent)), CircleShape),
                        )
                        ArtePokemon(
                            item, Modifier.align(Alignment.Center).fillMaxWidth(.66f).fillMaxHeight(.77f)
                                .graphicsLayer {
                                    alpha = entrada.value
                                    translationY = 14.dp.toPx() * (1f - entrada.value)
                                },
                            descripcion = item.titulo,
                        )
                        if (alternar != null) Corazon(item, alternar, Modifier.align(Alignment.TopEnd).padding(9.dp).size(52.dp))
                        Surface(
                            Modifier.align(Alignment.BottomStart).padding(10.dp),
                            shape = RoundedCornerShape(15.dp), color = Color(0xF2FFFFFF), contentColor = Color(0xFF153D31),
                        ) {
                            val label = if (documentado) habitat.nombre else "${habitat.nombre} · escena sugerida"
                            Text(label, Modifier.padding(horizontal = 11.dp, vertical = 7.dp), style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    Column(
                        Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(item.titulo, style = MaterialTheme.typography.displaySmall, textAlign = TextAlign.Center)
                        Text(
                            "Pokédex #${item.numero.toString().padStart(3, '0')}",
                            style = MaterialTheme.typography.labelMedium, color = colores.onSurfaceVariant,
                        )
                        perfil?.especie?.categoria?.let {
                            Text(it, style = MaterialTheme.typography.bodyMedium, color = colores.onSurfaceVariant)
                        }
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(7.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            item.tipos.forEach { EtiquetaTipo(it) }
                        }
                    }
                    if (perfil == null) {
                        Text("Conéctate para descargar la ficha completa.", color = colores.onSurfaceVariant)
                        TextButton(onClick = reintentar) { Text("Reintentar") }
                    } else {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            SeccionFicha.entries.forEach { opcion ->
                                FilterChip(
                                    selected = seccion == opcion, onClick = { seccion = opcion },
                                    modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                                    label = { Text(opcion.etiqueta, Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                                )
                            }
                        }
                        Crossfade(seccion, animationSpec = tween(180), label = "Sección de Pokédex") { tab ->
                            when (tab) {
                                SeccionFicha.FICHA -> Resumen(item, perfil, reintentar)
                                SeccionFicha.TECNICAS -> Tecnicas(perfil, consultarTecnica)
                                SeccionFicha.EVOLUCION -> Evoluciones(perfil, reintentar)
                            }
                        }
                    }
                }
                if (comparar != null) {
                    Surface(color = colores.background, shadowElevation = 3.dp) {
                        VidrioBoton(
                            comparar, Modifier.fillMaxWidth().padding(horizontal = 38.dp, vertical = 8.dp).heightIn(min = 60.dp),
                            forma = RoundedCornerShape(23.dp), destacado = true,
                        ) {
                            Row(
                                Modifier.padding(horizontal = 13.dp, vertical = 11.dp),
                                horizontalArrangement = Arrangement.spacedBy(9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icono(R.drawable.poke_comparar, null, tinte = colores.onPrimary)
                                Text(etiquetaComparar, color = colores.onPrimary, style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }
            Image(
                painterResource(habitat.borde), null,
                Modifier.matchParentSize().zIndex(1f).drawWithContent {
                    val margen = minOf(28.dp.toPx(), size.width * .13f)
                    val capa = this
                    clipRect(right = margen) { capa.drawContent() }
                    clipRect(left = size.width - margen) { capa.drawContent() }
                }.graphicsLayer {
                    alpha = .89f * entrada.value
                    translationY = 9.dp.toPx() * (1f - entrada.value)
                },
                contentScale = ContentScale.FillBounds,
            )
        }
    }
}

@Composable
internal fun EtiquetaTipo(id: String, sufijo: String = "") {
    val colores = MaterialTheme.colorScheme
    val planta = id == "grass"
    Surface(
        shape = RoundedCornerShape(13.dp),
        color = if (planta) colores.primaryContainer else colores.tertiaryContainer,
        contentColor = if (planta) colores.onPrimaryContainer else colores.onTertiaryContainer,
    ) {
        Text(nombreTipo(id) + sufijo, Modifier.padding(horizontal = 10.dp, vertical = 5.dp), style = MaterialTheme.typography.labelMedium)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Resumen(item: Item, perfil: PerfilPokemon, reintentar: () -> Unit) {
    val colores = MaterialTheme.colorScheme
    val numero = remember { NumberFormat.getNumberInstance() }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        perfil.especie?.descripcion?.let {
            Text(it, Modifier.fillMaxWidth(), color = colores.onSurfaceVariant, textAlign = TextAlign.Center)
        }
        FlowRow(
            Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(19.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item.alturaDm?.let { Indicador("Altura", numero.format(it / 10.0) + " m") }
            item.pesoHg?.let { Indicador("Peso", numero.format(it / 10.0) + " kg") }
            perfil.especie?.generacion?.let { Indicador("Generación", it) }
        }
        if (perfil.estadisticas.isNotEmpty()) {
            TituloCentrado("Estadísticas base")
            EstadisticasPokemon.forEach { (id, etiqueta) ->
                perfil.estadisticas[id]?.let { valor ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(etiqueta, style = MaterialTheme.typography.bodyMedium)
                            Text(valor.toString(), style = MaterialTheme.typography.labelLarge, color = colores.primary)
                        }
                        LinearProgressIndicator(
                            progress = { (valor / 255f).coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(5.dp),
                        )
                    }
                }
            }
        }
        val debilidades = perfil.afinidades?.filterValues { it > 1f }.orEmpty()
        if (debilidades.isNotEmpty()) {
            TituloCentrado("Débil frente a")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                debilidades.entries.sortedByDescending { it.value }.forEach { (tipo, factor) ->
                    EtiquetaTipo(tipo, " ×${numero.format(factor)}")
                }
            }
        }
        val resistencias = perfil.afinidades?.filterValues { it < 1f }.orEmpty()
        if (resistencias.isNotEmpty()) {
            TituloCentrado("Resistencias e inmunidades")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                resistencias.forEach { (tipo, factor) -> EtiquetaTipo(tipo, " ×${numero.format(factor)}") }
            }
        }
        if (!perfil.completo) TextButton(onClick = reintentar) { Text("Descargar datos faltantes") }
    }
}

@Composable
private fun Indicador(etiqueta: String, valor: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.titleLarge)
        Text(etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun TituloCentrado(texto: String) {
    Text(texto, Modifier.fillMaxWidth(), style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
}

@Composable
private fun Tecnicas(perfil: PerfilPokemon, consultar: ((String, Boolean) -> Unit)?) {
    var filtro by rememberSaveable { mutableStateOf("") }
    var limite by rememberSaveable { mutableIntStateOf(12) }
    val visibles = perfil.movimientos.filter { it.nombreLegible().contains(filtro.trim(), ignoreCase = true) }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TituloCentrado("Habilidades")
        perfil.habilidades.forEach { habilidad ->
            FilaTecnica(habilidad.id.nombreLegible(), if (habilidad.oculta) "Oculta" else null) {
                consultar?.invoke(habilidad.id, true)
            }
        }
        if (perfil.habilidades.isEmpty()) Text("Habilidades pendientes de descarga.")
        Spacer(Modifier.height(10.dp))
        TituloCentrado("Movimientos · ${perfil.movimientos.size}")
        if (perfil.movimientos.size > 12) {
            OutlinedTextField(
                filtro, { filtro = it; limite = 12 }, label = { Text("Buscar movimiento") },
                modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(16.dp),
            )
        }
        visibles.take(limite).forEach { movimiento ->
            FilaTecnica(movimiento.nombreLegible(), null) { consultar?.invoke(movimiento, false) }
        }
        if (visibles.isEmpty()) Text("Sin movimientos para mostrar.")
        if (visibles.size > limite) TextButton(onClick = { limite += 12 }) { Text("Ver más movimientos") }
    }
}

@Composable
private fun FilaTecnica(nombre: String, nota: String?, abrir: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 52.dp).clickable(role = Role.Button, onClick = abrir),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(nombre, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
        if (nota != null) Text(nota, color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.labelMedium)
        Text("›", Modifier.padding(horizontal = 8.dp), style = MaterialTheme.typography.titleLarge)
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun Evoluciones(perfil: PerfilPokemon, reintentar: () -> Unit) {
    val etapas = perfil.evolucion
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        TituloCentrado("Línea evolutiva")
        when {
            etapas == null -> {
                Text("La cadena todavía está pendiente de descarga.")
                TextButton(onClick = reintentar) { Text("Reintentar") }
            }
            etapas.size <= 1 -> Text("Esta especie no tiene otra etapa en su cadena.")
            else -> etapas.forEach { etapa ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(11.dp), verticalAlignment = Alignment.CenterVertically,
                ) {
                    val ilustracion = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${etapa.numero}.png"
                    ArtePokemon(Item(etapa.nombre, etapa.numero, ilustracion), Modifier.size(58.dp))
                    Column(Modifier.weight(1f)) {
                        Text(etapa.nombre.nombreLegible(), style = MaterialTheme.typography.titleMedium)
                        val origen = etapa.desde?.nombreLegible()?.let { "Desde $it" } ?: "Etapa inicial"
                        Text(listOf(origen, etapa.condiciones.joinToString(" o ")).filter(String::isNotBlank).joinToString(" · "),
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                HorizontalDivider()
            }
        }
        perfil.especie?.let { especie ->
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Indicador("Índice de captura", especie.captura.toString())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun DetalleTecnica(estado: UiState<FichaTecnica>, cerrar: () -> Unit) {
    ModalBottomSheet(onDismissRequest = cerrar) {
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            when (estado) {
                UiState.Cargando -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally).padding(18.dp))
                is UiState.Error -> Text(estado.mensaje, color = MaterialTheme.colorScheme.error)
                is UiState.Exito -> {
                    val ficha = estado.datos
                    Text(ficha.nombre, style = MaterialTheme.typography.headlineMedium)
                    ficha.tipo?.let { EtiquetaTipo(it) }
                    ficha.descripcion?.let { Text(it) }
                    ficha.categoria?.let {
                        val nombre = when (it) {
                            "physical" -> "Físico"
                            "special" -> "Especial"
                            "status" -> "Estado"
                            else -> it.nombreLegible()
                        }
                        Text(nombre, style = MaterialTheme.typography.labelLarge)
                    }
                    if (ficha.tipo != null) {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(22.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                            Indicador("Potencia", ficha.potencia?.toString() ?: "—")
                            Indicador("Precisión", ficha.precision?.let { "$it %" } ?: "—")
                            Indicador("PP", ficha.pp?.toString() ?: "—")
                        }
                    }
                }
            }
            TextButton(onClick = cerrar, modifier = Modifier.align(Alignment.End)) { Text("Cerrar") }
        }
    }
}
internal fun nombreTipo(tipo: String) = when (tipo) {
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

