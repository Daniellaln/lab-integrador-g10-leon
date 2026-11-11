package com.tecsup.pokemon.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.notification.Notificador
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.lista.mensajeDe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class Totales(val guardados: Int, val favoritos: Int)

class AdminViewModel(
    private val repo: ItemRepository,
    private val notificador: Notificador,
) : ViewModel() {
    val estado = repo.items
        .map { items -> UiState.Exito(Totales(items.size, items.count { it.esFavorito })) }
        .stateIn<UiState<Totales>>(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Cargando)

    private val _ocupado = MutableStateFlow(false)
    val ocupado = _ocupado.asStateFlow()
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()

    fun sincronizar() = ejecutar {
        repo.refrescar().fold(
            onSuccess = {
                val texto = "Se guardaron $it elementos"
                texto
            },
            onFailure = ::mensajeDe,
        )
    }

    fun limpiar() = ejecutar {
        repo.limpiarCache()
        "Se borraron los datos locales."
    }

    private fun ejecutar(accion: suspend () -> String) {
        if (_ocupado.value) return
        _ocupado.value = true
        _mensaje.value = null
        viewModelScope.launch {
            try {
                _mensaje.value = accion()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value = mensajeDe(e)
            } finally {
                _ocupado.value = false
            }
        }
    }
}
