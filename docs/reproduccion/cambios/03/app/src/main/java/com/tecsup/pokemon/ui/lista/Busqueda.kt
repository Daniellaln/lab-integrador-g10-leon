package com.tecsup.pokemon.ui.lista

import com.tecsup.pokemon.domain.model.Item
import java.text.Normalizer
import java.util.Locale

private val diacriticos = Regex("\\p{M}+")

private fun normalizar(texto: String) =
    Normalizer.normalize(texto, Normalizer.Form.NFD).replace(diacriticos, "")
        .lowercase(Locale.ROOT).replace('-', ' ').trim()

internal fun List<Item>.buscarPokemon(consulta: String): List<Item> {
    val texto = normalizar(consulta)
    if (texto.isBlank()) return this
    val numero = texto.removePrefix("#").toIntOrNull()
    return if (numero != null) filter { it.numero == numero }
    else filter { normalizar(it.titulo).contains(texto) }
}
