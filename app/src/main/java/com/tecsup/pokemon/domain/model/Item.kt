package com.tecsup.pokemon.domain.model

data class Item(
    val id: String,
    val numero: Int,
    val imagenUrl: String?,
    val esFavorito: Boolean = false,
    val alturaDm: Int? = null,
    val pesoHg: Int? = null,
    val tipos: List<String> = emptyList(),
) {
    val titulo: String
        get() = id.split('-').joinToString(" ") { parte -> parte.replaceFirstChar(Char::uppercaseChar) }
}
