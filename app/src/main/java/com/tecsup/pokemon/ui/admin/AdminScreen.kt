package com.tecsup.pokemon.ui.admin

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.tecsup.pokemon.R
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.components.Icono
import com.tecsup.pokemon.ui.components.MensajeCentrado
import com.tecsup.pokemon.ui.components.VidrioBoton

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminScreen(
    estado: UiState<Totales>,
    ocupado: Boolean,
    mensaje: String?,
    sincronizar: () -> Unit,
    limpiar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var confirmar by rememberSaveable { mutableStateOf(false) }
    val colores = MaterialTheme.colorScheme
    when (estado) {
        UiState.Cargando -> Cargando(modifier)
        is UiState.Error -> MensajeCentrado("No se pudieron leer los totales", estado.mensaje, modifier)
        is UiState.Exito -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Column(
                Modifier.widthIn(max = 660.dp).fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp).padding(top = 10.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(17.dp),
            ) {
                Image(painterResource(R.drawable.prado), null,
                    Modifier.fillMaxWidth().height(145.dp).clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop)
                Text("Datos del dispositivo", style = MaterialTheme.typography.titleLarge)
                FlowRow(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(42.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Total(estado.datos.guardados, "Guardados")
                    Total(estado.datos.favoritos, "Favoritos")
                }
                HorizontalDivider(color = colores.outlineVariant)
                VidrioBoton(
                    sincronizar, Modifier.fillMaxWidth().heightIn(min = 54.dp),
                    activo = true, habilitado = !ocupado,
                ) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (ocupado) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                        else Icono(R.drawable.poke_refrescar, null, tinte = colores.onPrimaryContainer)
                        Text(if (ocupado) "Procesando…" else "Sincronizar", style = MaterialTheme.typography.labelLarge)
                    }
                }
                TextButton(onClick = { confirmar = true }, enabled = !ocupado, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text("Limpiar caché", color = if (ocupado) colores.onSurfaceVariant else colores.error)
                }
                if (mensaje != null) {
                    Text(
                        mensaje, Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                        color = colores.onSurfaceVariant,
                    )
                }
            }
        }
    }
    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            title = { Text("¿Limpiar caché?") },
            text = { Text("Se eliminarán los Pokémon guardados y sus favoritos de este dispositivo.") },
            confirmButton = { TextButton(onClick = { confirmar = false; limpiar() }) { Text("Limpiar") } },
            dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun Total(valor: Int, texto: String) {
    Column(
        Modifier.widthIn(min = 98.dp).semantics(mergeDescendants = true) {},
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Text(valor.toString(), style = MaterialTheme.typography.headlineLarge)
        Text(texto, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
