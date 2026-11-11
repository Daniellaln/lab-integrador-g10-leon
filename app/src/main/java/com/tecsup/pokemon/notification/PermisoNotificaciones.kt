package com.tecsup.pokemon.notification

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.tecsup.pokemon.ui.components.Cargando

@Composable
fun PermisoNotificaciones(content: @Composable () -> Unit) {
    val contexto = LocalContext.current
    val preferencias = remember(contexto) { contexto.getSharedPreferences("permisos", 0) }
    var resuelto by rememberSaveable {
        mutableStateOf(
            Build.VERSION.SDK_INT < 33 ||
                ContextCompat.checkSelfPermission(contexto, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED ||
                preferencias.getBoolean("notificaciones_solicitadas", false),
        )
    }
    val solicitar = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        preferencias.edit().putBoolean("notificaciones_solicitadas", true).apply()
        resuelto = true
    }

    if (resuelto) {
        content()
    } else {
        LaunchedEffect(Unit) { solicitar.launch(Manifest.permission.POST_NOTIFICATIONS) }
        Cargando()
    }
}
