package com.tecsup.pokemon.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tecsup.pokemon.R

@Composable
fun Cargando(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            CircularProgressIndicator(Modifier.size(30.dp), strokeWidth = 3.dp)
            Text("Cargando…", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun MensajeCentrado(titulo: String, texto: String, modifier: Modifier = Modifier, reintentar: (() -> Unit)? = null) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            Modifier.widthIn(max = 360.dp).verticalScroll(rememberScrollState()).padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icono(if (reintentar != null) R.drawable.poke_red else R.drawable.ic_leaf, null, tinte = MaterialTheme.colorScheme.primary)
            Text(titulo, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
            Text(texto, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (reintentar != null) {
                VidrioBoton(reintentar, Modifier.heightIn(min = 50.dp), activo = true) {
                    Text("Reintentar", Modifier.padding(horizontal = 23.dp, vertical = 12.dp), style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
