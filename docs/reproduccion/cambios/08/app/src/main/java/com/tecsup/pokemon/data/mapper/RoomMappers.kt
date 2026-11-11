package com.tecsup.pokemon.data.mapper

import com.tecsup.pokemon.data.local.ItemEntity
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.domain.model.PerfilPokemon
import kotlinx.serialization.json.Json

fun ItemEntity.aModelo(incluirPerfil: Boolean = false): Item = Item(
    id, numero, imagenUrl, esFavorito, alturaDm, pesoHg,
    tipos.split(',').filter(String::isNotBlank),
    if (incluirPerfil && perfilJson.isNotBlank()) Json.decodeFromString<PerfilPokemon>(perfilJson) else null,
)

fun Item.aEntity(): ItemEntity = ItemEntity(
    id, numero, imagenUrl, esFavorito, alturaDm, pesoHg, tipos.joinToString(","),
    perfil?.let { Json.encodeToString(PerfilPokemon.serializer(), it) }.orEmpty(),
)
