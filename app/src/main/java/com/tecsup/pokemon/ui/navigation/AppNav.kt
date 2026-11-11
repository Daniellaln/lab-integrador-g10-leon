package com.tecsup.pokemon.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tecsup.pokemon.AppContainer
import com.tecsup.pokemon.ui.lista.ListaScreen
import com.tecsup.pokemon.ui.lista.ListaViewModel

@Composable
fun AppNav(container: AppContainer) {
    CatalogoNav(container)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoNav(
    container: AppContainer,
) {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(when (ruta) {
                        else -> "Pokédex Planta"
                    })
                },
            )
        },
    ) { padding ->
        NavHost(nav, startDestination = "catalogo", modifier = Modifier.padding(padding)) {
            composable("catalogo") {
                val vm: ListaViewModel = viewModel(factory = container.factory)
                val estado by vm.estado.collectAsStateWithLifecycle()
                ListaScreen(
                    estado, reintentar = vm::refrescar,
                )
            }
        }
    }
}
