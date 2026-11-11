package com.tecsup.pokemon.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.data.auth.AuthRepository
import com.tecsup.pokemon.domain.model.Sesion
import com.tecsup.pokemon.ui.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repo: AuthRepository) : ViewModel() {
    private val _estado = MutableStateFlow<UiState<Sesion?>>(UiState.Cargando)
    val estado = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                _estado.value = UiState.Exito(repo.sesionActual())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.value = UiState.Error(mensajeDe(e))
            }
        }
    }

    fun ingresar(correo: String, clave: String) {
        if (_estado.value is UiState.Cargando) return
        if (correo.isBlank() || clave.isBlank()) {
            _estado.value = UiState.Error("Completa el correo y la contraseña.")
            return
        }

        _estado.value = UiState.Cargando
        viewModelScope.launch {
            _estado.value = repo.iniciarSesion(correo.trim(), clave).fold(
                onSuccess = { UiState.Exito(it) },
                onFailure = { UiState.Error(mensajeDe(it)) },
            )
        }
    }

    fun salir() {
        repo.cerrarSesion()
        _estado.value = UiState.Exito(null)
    }

    private fun mensajeDe(error: Throwable): String = when (error) {
        is IllegalStateException -> error.message ?: "No se pudo abrir la sesión."
        else -> "No se pudo comprobar el rol de tu cuenta."
    }
}
