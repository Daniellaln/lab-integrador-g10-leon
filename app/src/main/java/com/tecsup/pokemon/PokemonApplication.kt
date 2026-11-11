package com.tecsup.pokemon

import android.app.Application

class PokemonApplication : Application() {
    val container by lazy { AppContainer(this) }
}
