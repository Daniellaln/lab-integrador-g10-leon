package com.tecsup.pokemon.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: String,
    val numero: Int,
    val imagenUrl: String?,
    val esFavorito: Boolean = false,
    val alturaDm: Int? = null,
    val pesoHg: Int? = null,
    val tipos: String = "",
)
