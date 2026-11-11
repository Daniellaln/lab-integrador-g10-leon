package com.tecsup.pokemon

import androidx.lifecycle.ViewModelStore
import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.notification.Notificador
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.lista.ListaViewModel
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListaViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    private val mensajes = mutableListOf<String>()
    private val bulbasaur = Item("bulbasaur", 1, null)

    @Before fun preparar() { Dispatchers.setMain(dispatcher) }

    @After fun cerrar() {
        store.clear()
        Dispatchers.resetMain()
    }

    private fun modelo(repo: RepoFalso): ListaViewModel =
        ListaViewModel(repo, Notificador { _, texto -> mensajes.add(texto) })
            .also { store.put("lista", it) }

    @Test fun cargaHastaRecibirDatosYNotificaUnaVez() = runTest(dispatcher) {
        val repo = RepoFalso()
        val respuesta = CompletableDeferred<Result<Int>>()
        repo.sincronizar = { respuesta.await() }
        val vm = modelo(repo)

        advanceUntilIdle()
        assertEquals(UiState.Cargando, vm.estado.value)
        assertEquals(emptyList<String>(), mensajes)

        repo.items.value = listOf(bulbasaur)
        respuesta.complete(Result.success(1))
        advanceUntilIdle()

        assertEquals(UiState.Exito(listOf(bulbasaur)), vm.estado.value)
        assertEquals(listOf("Se guardaron 1 elementos"), mensajes)
    }

    @Test fun sinCacheMuestraErrorYElReintentoRecuperaLaLista() = runTest(dispatcher) {
        val repo = RepoFalso()
        repo.sincronizar = { Result.failure(IOException("sin red")) }
        val vm = modelo(repo)
        advanceUntilIdle()

        assertEquals(UiState.Error("No se pudo conectar. Revisa tu conexión."), vm.estado.value)
        assertNull(vm.aviso.value)
        assertEquals(emptyList<String>(), mensajes)

        repo.sincronizar = {
            repo.items.value = listOf(bulbasaur)
            Result.success(1)
        }
        vm.refrescar()
        advanceUntilIdle()
        assertEquals(UiState.Exito(listOf(bulbasaur)), vm.estado.value)
        assertEquals(2, repo.intentos)
    }

    @Test fun sinRedMantieneLaCacheYElFavoritoYPublicaUnAviso() = runTest(dispatcher) {
        val favorito = bulbasaur.copy(esFavorito = true)
        val repo = RepoFalso(listOf(favorito))
        repo.sincronizar = { Result.failure(IOException("sin red")) }
        val vm = modelo(repo)

        advanceUntilIdle()
        assertEquals(UiState.Exito(listOf(favorito)), vm.estado.value)
        assertEquals("Sin conexión: mostrando datos guardados", vm.aviso.value)
        assertEquals(emptyList<String>(), mensajes)
    }

    private class RepoFalso(inicial: List<Item> = emptyList()) : ItemRepository {
        override val items = MutableStateFlow(inicial)
        override val favoritos = MutableStateFlow(inicial.filter(Item::esFavorito))
        var sincronizar: suspend () -> Result<Int> = { Result.success(0) }
        var intentos = 0
            private set

        override suspend fun refrescar(): Result<Int> {
            intentos++
            return sincronizar()
        }
        override suspend fun obtenerDetalle(id: String) = Result.success(items.value.first { it.id == id })
        override suspend fun alternarFavorito(id: String) {
            items.value = items.value.map { if (it.id == id) it.copy(esFavorito = !it.esFavorito) else it }
            favoritos.value = items.value.filter(Item::esFavorito)
        }
        override suspend fun cantidadGuardada() = items.value.size
        override suspend fun cantidadFavoritos() = favoritos.value.size
        override suspend fun limpiarCache() {
            items.value = emptyList()
            favoritos.value = emptyList()
        }
    }
}
