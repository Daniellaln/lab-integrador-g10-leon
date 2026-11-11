package com.tecsup.pokemon.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.components.MensajeCentrado

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

    when (estado) {
        UiState.Cargando -> Cargando(modifier)
        is UiState.Error -> MensajeCentrado("No se pudieron leer los totales", estado.mensaje, modifier)
        is UiState.Exito -> Column(
            modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Datos del dispositivo", style = MaterialTheme.typography.titleLarge)
            Text("Guardados: ${estado.datos.guardados}")
            Text("Favoritos: ${estado.datos.favoritos}")
            HorizontalDivider()
            Button(onClick = sincronizar, enabled = !ocupado, modifier = Modifier.heightIn(min = 48.dp)) {
                Text("Sincronizar")
            }
            OutlinedButton(
                onClick = { confirmar = true },
                enabled = !ocupado,
                modifier = Modifier.heightIn(min = 48.dp),
            ) { Text("Limpiar caché") }
            if (ocupado) LinearProgressIndicator(Modifier.fillMaxWidth())
            if (mensaje != null) Text(mensaje)
        }
    }

    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            title = { Text("¿Limpiar caché?") },
            text = { Text("Se eliminarán los Pokémon guardados y sus favoritos de este dispositivo.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmar = false
                    limpiar()
                }) { Text("Limpiar") }
            },
            dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Cancelar") } },
        )
    }
}
