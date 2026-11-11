package com.tecsup.pokemon.data.repository

import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.domain.model.FichaTecnica
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    val items: Flow<List<Item>>
    val favoritos: Flow<List<Item>>
    suspend fun refrescar(): Result<Int>
    suspend fun obtenerDetalle(id: String): Result<Item>
    suspend fun obtenerTecnica(id: String, habilidad: Boolean): Result<FichaTecnica>
    suspend fun alternarFavorito(id: String)
    suspend fun cantidadGuardada(): Int
    suspend fun cantidadFavoritos(): Int
    suspend fun limpiarCache()
}
