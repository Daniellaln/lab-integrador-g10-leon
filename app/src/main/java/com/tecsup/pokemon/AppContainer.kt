package com.tecsup.pokemon

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tecsup.pokemon.data.local.AppDatabase
import com.tecsup.pokemon.data.remote.RetrofitClient
import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.data.repository.ItemRepositoryImpl
import com.tecsup.pokemon.notification.Notificador
import com.tecsup.pokemon.ui.detalle.DetalleViewModel
import com.tecsup.pokemon.ui.lista.ListaViewModel

class AppContainer(context: Context) {
    private val repositorio: ItemRepository by lazy {
        ItemRepositoryImpl(RetrofitClient.crear(), AppDatabase.obtener(context.applicationContext))
    }
    private val notificador = Notificador { _, _ -> }

    val factory: ViewModelProvider.Factory = viewModelFactory {
        initializer { ListaViewModel(repositorio, notificador) }
    }

    fun detalleFactory(id: String): ViewModelProvider.Factory = viewModelFactory {
        initializer { DetalleViewModel(repositorio, id) }
    }
}
