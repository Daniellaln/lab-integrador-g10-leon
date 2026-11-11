package com.tecsup.pokemon.data.repository

import com.tecsup.pokemon.data.mapper.aModelo
import com.tecsup.pokemon.data.remote.ApiService
import com.tecsup.pokemon.domain.model.Item
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import retrofit2.HttpException

class ItemRepositoryImpl(private val api: ApiService) : ItemRepository {
    private val catalogo = MutableStateFlow<List<Item>>(emptyList())
    override val items = catalogo
    override val favoritos = flowOf(emptyList<Item>())

    override suspend fun refrescar(): Result<Int> = try {
        val datos = api.listar().pokemon.map { it.aModelo() }.distinctBy(Item::id).sortedBy(Item::numero)
        catalogo.value = datos
        Result.success(datos.size)
    } catch (e: IOException) {
        Result.failure(e)
    } catch (e: HttpException) {
        Result.failure(e)
    } catch (e: IllegalArgumentException) {
        Result.failure(e)
    }

    override suspend fun obtenerDetalle(id: String): Result<Item> = try {
        Result.success(api.detalle(id).aModelo())
    } catch (e: IOException) {
        Result.failure(e)
    } catch (e: HttpException) {
        Result.failure(e)
    } catch (e: IllegalArgumentException) {
        Result.failure(e)
    }

    override suspend fun alternarFavorito(id: String) = Unit
    override suspend fun cantidadGuardada(): Int = catalogo.value.size
    override suspend fun cantidadFavoritos(): Int = 0
    override suspend fun limpiarCache() { catalogo.value = emptyList() }
}
