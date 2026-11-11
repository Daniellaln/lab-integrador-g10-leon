package com.tecsup.pokemon

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.FirebaseApp
import com.tecsup.pokemon.data.auth.AuthRepository
import com.tecsup.pokemon.data.auth.FirebaseAuthRepository
import com.tecsup.pokemon.data.local.AppDatabase
import com.tecsup.pokemon.data.remote.RetrofitClient
import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.data.repository.ItemRepositoryImpl
import com.tecsup.pokemon.notification.Notificador
import com.tecsup.pokemon.notification.NotificadorAndroid
import com.tecsup.pokemon.ui.admin.AdminViewModel
import com.tecsup.pokemon.ui.detalle.DetalleViewModel
import com.tecsup.pokemon.ui.favoritos.FavoritosViewModel
import com.tecsup.pokemon.ui.lista.ListaViewModel
import com.tecsup.pokemon.ui.login.LoginViewModel

class AppContainer(context: Context) {
    private val repositorio: ItemRepository by lazy {
        ItemRepositoryImpl(RetrofitClient.crear(), AppDatabase.obtener(context.applicationContext))
    }
    private val notificador: Notificador = NotificadorAndroid(context.applicationContext)
    private val auth: AuthRepository by lazy {
        val app = FirebaseApp.getApps(context.applicationContext)
            .firstOrNull { it.name == FirebaseApp.DEFAULT_APP_NAME }
        FirebaseAuthRepository(app)
    }

    val factory: ViewModelProvider.Factory = viewModelFactory {
        initializer { ListaViewModel(repositorio, notificador) }
        initializer { FavoritosViewModel(repositorio) }
        initializer { LoginViewModel(auth) }
        initializer { AdminViewModel(repositorio, notificador) }
    }

    fun detalleFactory(id: String): ViewModelProvider.Factory = viewModelFactory {
        initializer { DetalleViewModel(repositorio, id) }
    }
}
