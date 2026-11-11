package com.tecsup.pokemon.notification

fun interface Notificador {
    fun mostrar(titulo: String, texto: String)
}
