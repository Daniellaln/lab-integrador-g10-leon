package com.tecsup.pokemon.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun Icono(@DrawableRes recurso: Int, descripcion: String?, modifier: Modifier = Modifier, tinte: Color = MaterialTheme.colorScheme.onSurface) {
    Icon(painterResource(recurso), descripcion, modifier.size(23.dp), tint = tinte)
}

@Composable
fun Modifier.vidrio(
    forma: Shape = RoundedCornerShape(30.dp),
    activo: Boolean = false,
    reflejo: Float = 0f,
    enfocado: Boolean = false,
    destacado: Boolean = false,
): Modifier {
    val colores = MaterialTheme.colorScheme
    val oscuro = colores.surface.luminance() < .3f
    val luz = if (oscuro) Color(0xFFCCE6D2) else Color.White
    val base = when {
        destacado -> colores.primary
        activo -> colores.primaryContainer
        else -> colores.surface
    }
    val borde = if (enfocado) colores.primary else luz.copy(alpha = if (oscuro) .32f else .96f)
    return clip(forma)
        .background(Brush.verticalGradient(listOf(
            if (destacado) base else luz.copy(alpha = if (oscuro) .20f else .80f),
            base.copy(alpha = if (destacado || activo) .96f else .78f),
            base.copy(alpha = .94f),
        )))
        .background(Brush.radialGradient(
            listOf(luz.copy(alpha = .18f + .22f * reflejo), Color.Transparent),
            center = Offset(20f + 66f * reflejo, 0f),
            radius = 120f,
        ))
        .border(1.dp, Brush.linearGradient(listOf(borde, colores.outlineVariant, borde)), forma)
}

@Composable
fun VidrioBoton(
    pulsar: () -> Unit,
    modifier: Modifier = Modifier,
    activo: Boolean = false,
    habilitado: Boolean = true,
    forma: Shape = CircleShape,
    destacado: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    val foco by interaccion.collectIsFocusedAsState()
    val avance by animateFloatAsState(if (presionado) 1f else 0f, spring(.72f, 640f), label = "Reflejo")
    Box(
        modifier
            .graphicsLayer {
                scaleX = 1f + .021f * avance
                scaleY = 1f - .070f * avance
                alpha = if (habilitado) 1f else .58f
            }
            .vidrio(forma, activo, avance, foco, destacado)
            .clickable(
                interactionSource = interaccion, indication = null, enabled = habilitado,
                role = Role.Button, onClick = pulsar,
            ),
        contentAlignment = Alignment.Center,
        content = content,
    )
}
