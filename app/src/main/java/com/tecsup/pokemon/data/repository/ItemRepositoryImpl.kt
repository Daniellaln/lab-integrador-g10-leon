package com.tecsup.pokemon.data.repository

import androidx.room.withTransaction
import com.tecsup.pokemon.data.local.AppDatabase
import com.tecsup.pokemon.data.mapper.aEntity
import com.tecsup.pokemon.data.mapper.aModelo
import com.tecsup.pokemon.data.remote.ApiService
import com.tecsup.pokemon.domain.model.Item
import java.io.IOException
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException

class ItemRepositoryImpl(
    private val api: ApiService,
    private val db: AppDatabase,
) : ItemRepository {
    private val dao = db.items()
    private val operaciones = Mutex()
    override val items = dao.todos().map { filas -> filas.map { it.aModelo() } }
    override val favoritos = dao.favoritos().map { filas -> filas.map { it.aModelo() } }

    override suspend fun refrescar(): Result<Int> = operaciones.withLock {
        try {
            val nuevos = api.listar().pokemon.map { it.aModelo() }.distinctBy(Item::id)
            db.withTransaction {
                val previos = dao.copia().associateBy { it.id }
                dao.guardarTodos(nuevos.map { pokemon ->
                    previos[pokemon.id]?.copy(numero = pokemon.numero) ?: pokemon.aEntity()
                })
            }
            Result.success(nuevos.size)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        }
    }

    override suspend fun obtenerDetalle(id: String): Result<Item> = operaciones.withLock {
        val anterior = dao.buscar(id)
        try {
            val detalle = api.detalle(id).aModelo().copy(esFavorito = anterior?.esFavorito ?: false)
            dao.guardar(detalle.aEntity())
            Result.success(detalle)
        } catch (e: IOException) {
            anterior?.aModelo()?.let { Result.success(it) } ?: Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        }
    }

    override suspend fun alternarFavorito(id: String) {
        operaciones.withLock { dao.alternarFavorito(id) }
    }

    override suspend fun cantidadGuardada(): Int = dao.contar()

    override suspend fun cantidadFavoritos(): Int = dao.contarFavoritos()

    override suspend fun limpiarCache() {
        operaciones.withLock { dao.borrarTodo() }
    }
}
