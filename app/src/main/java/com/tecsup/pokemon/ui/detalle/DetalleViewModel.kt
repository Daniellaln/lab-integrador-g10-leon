package com.tecsup.pokemon.ui.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.lista.mensajeDe
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetalleViewModel(private val repo: ItemRepository, private val id: String) : ViewModel() {
    private val detalle = MutableStateFlow<UiState<Item>>(UiState.Cargando)
    private var carga: Job? = null
    val estado = combine(detalle, repo.items) { actual, items ->
        when (actual) {
            is UiState.Exito -> {
                val favorito = items.firstOrNull { it.id == id }?.esFavorito ?: actual.datos.esFavorito
                UiState.Exito(actual.datos.copy(esFavorito = favorito))
            }
            else -> actual
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Cargando)

    init {
        cargar()
    }

    fun cargar() {
        if (carga?.isActive == true) return
        carga = viewModelScope.launch {
            detalle.value = UiState.Cargando
            detalle.value = repo.obtenerDetalle(id).fold(
                onSuccess = { UiState.Exito(it) },
                onFailure = { UiState.Error(mensajeDe(it)) },
            )
        }
    }

    fun alternarFavorito() {
        viewModelScope.launch { repo.alternarFavorito(id) }
    }
}
