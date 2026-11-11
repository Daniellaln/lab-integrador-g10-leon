package com.tecsup.pokemon.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.notification.Notificador
import com.tecsup.pokemon.ui.UiState
import java.io.IOException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ListaViewModel(
    private val repo: ItemRepository,
    private val notificador: Notificador,
) : ViewModel() {
    private val carga = MutableStateFlow<UiState<Unit>>(UiState.Cargando)
    private val _aviso = MutableStateFlow<String?>(null)
    val aviso = _aviso.asStateFlow()
    private var refresco: Job? = null

    val estado = combine(repo.items, carga) { items, actual ->
        when {
            items.isNotEmpty() -> UiState.Exito(items)
            actual is UiState.Error -> actual
            actual is UiState.Cargando -> UiState.Cargando
            else -> UiState.Exito(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Cargando)

    init {
        refrescar()
    }

    fun refrescar() {
        if (refresco?.isActive == true) return
        refresco = viewModelScope.launch {
            carga.value = UiState.Cargando
            _aviso.value = null
            repo.refrescar().fold(
                onSuccess = { guardados ->
                    carga.value = UiState.Exito(Unit)
                },
                onFailure = { error ->
                    carga.value = UiState.Error(mensajeDe(error))
                },
            )
        }
    }

    fun alternarFavorito(id: String) {
        viewModelScope.launch { repo.alternarFavorito(id) }
    }
}

internal fun mensajeDe(error: Throwable): String = when (error) {
    is IOException -> "No se pudo conectar. Revisa tu conexión."
    is HttpException -> "PokéAPI devolvió un error (${error.code()})."
    else -> "No se pudieron cargar los datos."
}
