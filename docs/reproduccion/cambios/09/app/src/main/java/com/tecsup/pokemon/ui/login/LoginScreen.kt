package com.tecsup.pokemon.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.tecsup.pokemon.R
import com.tecsup.pokemon.domain.model.Sesion
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.components.Icono
import com.tecsup.pokemon.ui.components.VidrioBoton

@Composable
fun LoginScreen(estado: UiState<Sesion?>, ingresar: (String, String) -> Unit) {
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    val ocupado = estado is UiState.Cargando
    val colores = MaterialTheme.colorScheme
    val foco = LocalFocusManager.current
    val enviar = {
        foco.clearFocus()
        if (!ocupado) ingresar(correo, clave)
    }

    BoxWithConstraints(Modifier.fillMaxSize().background(colores.background).safeDrawingPadding().imePadding()) {
        val altura = (maxHeight * .43f).coerceIn(160.dp, 360.dp)
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.fillMaxWidth().height(altura)) {
                Image(
                    painterResource(R.drawable.hab_invernadero), null, Modifier.fillMaxSize(),
                    alignment = BiasAlignment(0f, -.95f), contentScale = ContentScale.Crop,
                )
                if (colores.background.luminance() < .3f) {
                    Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = .38f)))
                }
                Box(Modifier.matchParentSize().background(Brush.verticalGradient(
                    0f to Color.Transparent, .65f to Color.Transparent, 1f to colores.background,
                )))
                Image(
                    painterResource(R.drawable.poke_marca), null,
                    Modifier.align(Alignment.TopStart).padding(22.dp).size(48.dp),
                )
            }
            Column(
                Modifier.widthIn(max = 440.dp).fillMaxWidth().padding(horizontal = 27.dp).padding(bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("Pokédex Planta", style = MaterialTheme.typography.displaySmall, color = colores.onBackground)
                Text("Inicia sesión para abrir tu catálogo.", color = colores.onSurfaceVariant)
                Spacer(Modifier.height(3.dp))
                val forma = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                OutlinedTextField(
                    value = correo, onValueChange = { correo = it },
                    label = { Text("Correo electrónico") }, modifier = Modifier.fillMaxWidth(),
                    shape = forma, singleLine = true, enabled = !ocupado,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                )
                OutlinedTextField(
                    value = clave, onValueChange = { clave = it },
                    label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(),
                    shape = forma, singleLine = true, enabled = !ocupado,
                    visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { enviar() }),
                    trailingIcon = {
                        IconButton(onClick = { visible = !visible }, enabled = !ocupado) {
                            Icono(
                                if (visible) R.drawable.poke_ojo_cerrado else R.drawable.poke_ojo,
                                if (visible) "Ocultar contraseña" else "Mostrar contraseña",
                            )
                        }
                    },
                )
                if (estado is UiState.Error) Text(estado.mensaje, color = colores.error)
                VidrioBoton(enviar, Modifier.fillMaxWidth().heightIn(min = 62.dp), destacado = true, habilitado = !ocupado) {
                    Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (ocupado) CircularProgressIndicator(Modifier.size(17.dp), color = colores.onPrimary, strokeWidth = 2.dp)
                        Text(if (ocupado) "Comprobando sesión…" else "Ingresar", color = colores.onPrimary, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}
