package com.tecsup.pokemon.data.mapper

import com.tecsup.pokemon.data.local.ItemEntity
import com.tecsup.pokemon.domain.model.Item

fun ItemEntity.aModelo(): Item = Item(
    id, numero, imagenUrl, esFavorito, alturaDm, pesoHg,
    tipos.split(',').filter(String::isNotBlank),
)

fun Item.aEntity(): ItemEntity = ItemEntity(
    id, numero, imagenUrl, esFavorito, alturaDm, pesoHg, tipos.joinToString(","),
)
