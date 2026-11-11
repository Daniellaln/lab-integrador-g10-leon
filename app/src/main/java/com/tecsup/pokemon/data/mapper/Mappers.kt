package com.tecsup.pokemon.data.mapper

import com.tecsup.pokemon.data.remote.dto.EntradaTipoDto
import com.tecsup.pokemon.data.remote.dto.ItemDto
import com.tecsup.pokemon.domain.model.Item

private fun arteOficial(numero: Int) =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$numero.png"

fun EntradaTipoDto.aModelo(): Item {
    val numero = pokemon.url.trimEnd('/').substringAfterLast('/').toInt()
    return Item(id = pokemon.name, numero = numero, imagenUrl = arteOficial(numero))
}

fun ItemDto.aModelo(): Item = Item(
    id = name,
    numero = id,
    imagenUrl = sprites.other?.arte?.frente ?: sprites.frente ?: arteOficial(id),
    alturaDm = height,
    pesoHg = weight,
    tipos = types.map { it.type.name },
)
