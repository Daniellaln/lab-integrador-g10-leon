package com.tecsup.pokemon.ui.navigation

import android.net.Uri
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
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.detalle.DetalleScreen
import com.tecsup.pokemon.ui.detalle.DetalleViewModel
import com.tecsup.pokemon.ui.favoritos.FavoritosScreen
import com.tecsup.pokemon.ui.favoritos.FavoritosViewModel
import com.tecsup.pokemon.ui.lista.ListaScreen
import com.tecsup.pokemon.ui.lista.ListaViewModel
import com.tecsup.pokemon.ui.login.LoginScreen
import com.tecsup.pokemon.ui.login.LoginViewModel

@Composable
fun AppNav(container: AppContainer) {
    val login: LoginViewModel = viewModel(factory = container.factory)
    val estado by login.estado.collectAsStateWithLifecycle()
    val sesion = (estado as? UiState.Exito)?.datos
    Surface {
        if (sesion == null) {
            LoginScreen(estado, login::ingresar)
        } else {
            key(sesion.uid) {
                CatalogoNav(container, login::salir)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoNav(
    container: AppContainer,
    salir: () -> Unit,
) {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route
    val enDetalle = ruta == "detalle/{id}"
    val abrir: (String) -> Unit = { id ->
        nav.navigate("detalle/${Uri.encode(id)}") { launchSingleTop = true }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(when (ruta) {
                        "favoritos" -> "Favoritos"
                        "detalle/{id}" -> "Ficha Pokémon"
                        else -> "Pokédex Planta"
                    })
                },
                navigationIcon = {
                    if (enDetalle) TextButton(onClick = { nav.popBackStack() }) { Text("Volver") }
                },
                actions = {
                    if (!enDetalle) TextButton(onClick = salir) { Text("Cerrar sesión") }
                },
            )
        },
        bottomBar = {
            if (!enDetalle) {
                NavigationBar {
                    NavigationBarItem(
                        selected = ruta == "catalogo",
                        onClick = { nav.popBackStack("catalogo", inclusive = false) },
                        icon = { Text("P") },
                        label = { Text("Catálogo") },
                    )
                    NavigationBarItem(
                        selected = ruta == "favoritos",
                        onClick = {
                            nav.navigate("favoritos") {
                                popUpTo("catalogo")
                                launchSingleTop = true
                            }
                        },
                        icon = { Text("F") },
                        label = { Text("Favoritos") },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(nav, startDestination = "catalogo", modifier = Modifier.padding(padding)) {
            composable("catalogo") {
                val vm: ListaViewModel = viewModel(factory = container.factory)
                val estado by vm.estado.collectAsStateWithLifecycle()
                val aviso by vm.aviso.collectAsStateWithLifecycle()
                ListaScreen(
                    estado, aviso = aviso, reintentar = vm::refrescar,
                    abrir = abrir, alternar = vm::alternarFavorito,
                )
            }
            composable("detalle/{id}") { entradaDetalle ->
                val id = requireNotNull(entradaDetalle.arguments?.getString("id"))
                val vm: DetalleViewModel = viewModel(factory = container.detalleFactory(id))
                val estado by vm.estado.collectAsStateWithLifecycle()
                DetalleScreen(estado, vm::cargar, vm::alternarFavorito)
            }
            composable("favoritos") {
                val vm: FavoritosViewModel = viewModel(factory = container.factory)
                val estado by vm.estado.collectAsStateWithLifecycle()
                FavoritosScreen(estado, abrir, vm::alternarFavorito)
            }
        }
    }
}
