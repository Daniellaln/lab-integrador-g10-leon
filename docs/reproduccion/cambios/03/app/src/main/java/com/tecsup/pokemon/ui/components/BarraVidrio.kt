package com.tecsup.pokemon.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class Destino(val ruta: String, val etiqueta: String, @param:DrawableRes val icono: Int)

@Composable
fun BarraVidrio(destinos: List<Destino>, actual: String, elegir: (String) -> Unit) {
    val forma = RoundedCornerShape(32.dp)
    val colores = MaterialTheme.colorScheme
    BoxWithConstraints(
        Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 18.dp, vertical = 7.dp)
            .shadow(8.dp, forma, spotColor = colores.onSurface.copy(alpha = .25f))
            .vidrio(forma).padding(4.dp).selectableGroup(),
    ) {
        val ancho = maxWidth / destinos.size
        val indice = destinos.indexOfFirst { it.ruta == actual }.coerceAtLeast(0)
        val x by animateDpAsState(ancho * indice, spring(.82f, 460f), label = "Pestaña de vidrio")
        Box(Modifier.align(Alignment.CenterStart).offset(x = x).width(ancho).height(58.dp)
            .padding(horizontal = 2.dp).vidrio(RoundedCornerShape(27.dp), activo = true))
        Row(Modifier.fillMaxWidth()) {
            destinos.forEach { destino ->
                val seleccionado = destino.ruta == actual
                val color = if (seleccionado) colores.onPrimaryContainer else colores.onSurfaceVariant
                Column(
                    Modifier.weight(1f).heightIn(min = 60.dp)
                        .selectable(seleccionado, role = Role.Tab, onClick = { elegir(destino.ruta) })
                        .padding(horizontal = 3.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icono(destino.icono, null, tinte = color)
                    Text(destino.etiqueta, style = MaterialTheme.typography.labelMedium, color = color, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
