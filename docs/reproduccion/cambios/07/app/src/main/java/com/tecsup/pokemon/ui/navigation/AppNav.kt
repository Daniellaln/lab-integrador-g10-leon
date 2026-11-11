package com.tecsup.pokemon.ui.navigation

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tecsup.pokemon.AppContainer
import com.tecsup.pokemon.R
import com.tecsup.pokemon.domain.model.Rol
import com.tecsup.pokemon.notification.PermisoNotificaciones
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.admin.AdminScreen
import com.tecsup.pokemon.ui.admin.AdminViewModel
import com.tecsup.pokemon.ui.components.BarraVidrio
import com.tecsup.pokemon.ui.components.Destino
import com.tecsup.pokemon.ui.components.Icono
import com.tecsup.pokemon.ui.components.VidrioBoton
import com.tecsup.pokemon.ui.comparar.CompararScreen
import com.tecsup.pokemon.ui.detalle.DetalleScreen
import com.tecsup.pokemon.ui.detalle.DetalleViewModel
import com.tecsup.pokemon.ui.favoritos.FavoritosScreen
import com.tecsup.pokemon.ui.favoritos.FavoritosViewModel
import com.tecsup.pokemon.ui.lista.ListaScreen
import com.tecsup.pokemon.ui.lista.ListaViewModel
import com.tecsup.pokemon.ui.login.LoginScreen
import com.tecsup.pokemon.ui.login.LoginViewModel
import kotlinx.coroutines.launch

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
                PermisoNotificaciones {
                    CatalogoNav(container, sesion.rol == Rol.ADMIN, login::salir)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoNav(container: AppContainer, admin: Boolean, salir: () -> Unit) {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route ?: "catalogo"
    val pantallaSecundaria = ruta == "detalle/{id}" || ruta == "comparar/{primero}/{segundo}"
    var menu by rememberSaveable { mutableStateOf(false) }
    var seleccionado by rememberSaveable { mutableStateOf<String?>(null) }
    var revisionCatalogo by rememberSaveable { mutableIntStateOf(0) }
    val mensajes = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val destinos = remember(admin) {
        buildList {
            add(Destino("catalogo", "Catálogo", R.drawable.poke_libro))
            add(Destino("favoritos", "Favoritos", R.drawable.poke_corazon))
            if (admin) add(Destino("admin", "Admin", R.drawable.poke_admin))
        }
    }
    val elegir: (String) -> Unit = { destino ->
        if (destino != ruta) {
            if (destino == "catalogo") nav.popBackStack("catalogo", inclusive = false)
            else nav.navigate(destino) {
                popUpTo("catalogo")
                launchSingleTop = true
            }
        }
    }
    val abrir: (String) -> Unit = { id ->
        nav.navigate("detalle/${Uri.encode(id)}") { launchSingleTop = true }
    }
    val colores = MaterialTheme.colorScheme
    val teclado = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    BoxWithConstraints(Modifier.fillMaxSize().background(Brush.verticalGradient(
        listOf(colores.background, colores.background, colores.secondaryContainer),
    ))) {
        val pantallaAncha = maxWidth >= 600.dp
        val tituloCompacto = maxWidth < 380.dp
        Row(Modifier.fillMaxSize()) {
            if (pantallaAncha && !pantallaSecundaria) {
                NavigationRail(
                    containerColor = colores.surface,
                    modifier = Modifier.fillMaxHeight(),
                ) {
                    destinos.forEach { destino ->
                        NavigationRailItem(
                            selected = ruta == destino.ruta, onClick = { elegir(destino.ruta) },
                            icon = { Icono(destino.icono, null) }, label = { Text(destino.etiqueta) },
                        )
                    }
                }
            }
            Scaffold(
                modifier = Modifier.weight(1f).imePadding(),
                containerColor = Color.Transparent,
                topBar = {
                    val titulo = when (ruta) {
                        "favoritos" -> "Favoritos"
                        "admin" -> "Panel Admin"
                        "detalle/{id}" -> "Ficha Pokémon"
                        "comparar/{primero}/{segundo}" -> "Comparar Pokémon"
                        else -> "Pokédex Planta"
                    }
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                        title = {
                            Text(
                                titulo,
                                style = if (tituloCompacto) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium,
                                maxLines = 1, overflow = TextOverflow.Ellipsis,
                            )
                        },
                        navigationIcon = {
                            if (pantallaSecundaria) {
                                VidrioBoton({ nav.popBackStack() }, Modifier.padding(start = 8.dp, end = 4.dp).size(48.dp)) {
                                    Icono(R.drawable.poke_volver, "Volver")
                                }
                            } else {
                                Icono(R.drawable.ic_leaf, null, Modifier.padding(start = 17.dp, end = 9.dp), colores.primary)
                            }
                        },
                        actions = {
                            if (!pantallaSecundaria) {
                                Box(Modifier.padding(end = 16.dp)) {
                                    VidrioBoton({ menu = true }, Modifier.size(48.dp)) { Icono(R.drawable.poke_mas, "Opciones de sesión") }
                                    DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                                        DropdownMenuItem(text = { Text("Cerrar sesión") }, onClick = {
                                            menu = false
                                            salir()
                                        })
                                    }
                                }
                            }
                        },
                    )
                },
                snackbarHost = { SnackbarHost(mensajes) },
                bottomBar = {
                    if (!pantallaSecundaria && !pantallaAncha && !teclado) BarraVidrio(destinos, ruta, elegir)
                },
            ) { padding ->
                NavHost(nav, startDestination = "catalogo", modifier = Modifier.padding(padding)) {
                    composable("catalogo") {
                        val vm: ListaViewModel = viewModel(factory = container.factory)
                        val estado by vm.estado.collectAsStateWithLifecycle()
                        val aviso by vm.aviso.collectAsStateWithLifecycle()
                        key(revisionCatalogo) {
                            ListaScreen(estado, aviso = aviso, reintentar = vm::refrescar, abrir = abrir, alternar = vm::alternarFavorito)
                        }
                    }
                    composable("detalle/{id}") { entradaDetalle ->
                        val id = requireNotNull(entradaDetalle.arguments?.getString("id"))
                        val vm: DetalleViewModel = viewModel(factory = container.detalleFactory(id))
                        val estado by vm.estado.collectAsStateWithLifecycle()
                        val primero = seleccionado
                        val etiqueta = when (primero) {
                            null -> "Elegir para comparar"
                            id -> "Quitar de la comparación"
                            else -> "Comparar con el primero"
                        }
                        DetalleScreen(estado, vm::cargar, vm::alternarFavorito, etiquetaComparar = etiqueta) {
                            when (primero) {
                                null -> {
                                    seleccionado = id
                                    revisionCatalogo++
                                    elegir("catalogo")
                                    scope.launch { mensajes.showSnackbar("Pokémon elegido. Abre otra ficha para comparar.") }
                                }
                                id -> seleccionado = null
                                else -> nav.navigate("comparar/${Uri.encode(primero)}/${Uri.encode(id)}") {
                                    launchSingleTop = true
                                }
                            }
                        }
                    }
                    composable("comparar/{primero}/{segundo}") { argumentos ->
                        val primero = requireNotNull(argumentos.arguments?.getString("primero"))
                        val segundo = requireNotNull(argumentos.arguments?.getString("segundo"))
                        val a: DetalleViewModel = viewModel(key = primero, factory = container.detalleFactory(primero))
                        val b: DetalleViewModel = viewModel(key = segundo, factory = container.detalleFactory(segundo))
                        val estadoA by a.estado.collectAsStateWithLifecycle()
                        val estadoB by b.estado.collectAsStateWithLifecycle()
                        CompararScreen(estadoA, estadoB, reintentar = {
                            a.cargar()
                            b.cargar()
                        }, elegirOtros = {
                            seleccionado = null
                            revisionCatalogo++
                            elegir("catalogo")
                        })
                    }
                    composable("favoritos") {
                        val vm: FavoritosViewModel = viewModel(factory = container.factory)
                        val estado by vm.estado.collectAsStateWithLifecycle()
                        FavoritosScreen(estado, abrir, vm::alternarFavorito, explorar = { elegir("catalogo") })
                    }
                    if (admin) {
                        composable("admin") {
                            val vm: AdminViewModel = viewModel(factory = container.factory)
                            val estado by vm.estado.collectAsStateWithLifecycle()
                            val ocupado by vm.ocupado.collectAsStateWithLifecycle()
                            val mensaje by vm.mensaje.collectAsStateWithLifecycle()
                            AdminScreen(estado, ocupado, mensaje, vm::sincronizar, vm::limpiar)
                        }
                    }
                }
            }
        }
    }
}
