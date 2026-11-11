package com.tecsup.pokemon.data.repository

import androidx.room.withTransaction
import com.tecsup.pokemon.data.local.AppDatabase
import com.tecsup.pokemon.data.mapper.aEntity
import com.tecsup.pokemon.data.mapper.aModelo
import com.tecsup.pokemon.data.mapper.afinidades
import com.tecsup.pokemon.data.mapper.etapas
import com.tecsup.pokemon.data.remote.ApiService
import com.tecsup.pokemon.data.remote.dto.RelacionesDto
import com.tecsup.pokemon.domain.model.FichaTecnica
import com.tecsup.pokemon.domain.model.Item
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
    private val relaciones = ConcurrentHashMap<String, RelacionesDto>()
    private val tecnicas = ConcurrentHashMap<Pair<String, Boolean>, FichaTecnica>()
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
        val anterior = dao.buscar(id)?.aModelo(incluirPerfil = true)
        if (anterior?.perfil?.completo == true) return@withLock Result.success(anterior)
        val resultado = intentarApi {
            val dto = api.detalle(id)
            val detalle = dto.aModelo()
            val base = requireNotNull(detalle.perfil)
            val perfil = coroutineScope {
                val afinidades = async { afinidadesDe(detalle.tipos) }
                val especie = dto.species?.name?.let { nombre -> intentarApi { api.especie(nombre) }.getOrNull() }
                val cadena = especie?.evolucion?.url?.trimEnd('/')?.substringAfterLast('/')
                val etapas = when {
                    especie == null -> null
                    cadena == null -> emptyList()
                    else -> intentarApi { api.evolucion(cadena).chain.etapas() }.getOrNull()
                }
                base.copy(
                    especie = especie?.aModelo() ?: anterior?.perfil?.especie,
                    evolucion = etapas ?: anterior?.perfil?.evolucion,
                    afinidades = afinidades.await() ?: anterior?.perfil?.afinidades,
                )
            }
            detalle.copy(perfil = perfil, esFavorito = anterior?.esFavorito ?: false)
        }
        resultado.onSuccess { dao.guardar(it.aEntity()) }
            .recoverCatching { error -> anterior ?: throw error }
    }

    private suspend fun afinidadesDe(tipos: List<String>): Map<String, Float>? = coroutineScope {
        if (tipos.isEmpty()) return@coroutineScope null
        val respuestas = tipos.map { tipo ->
            async {
                relaciones[tipo] ?: intentarApi { api.tipo(tipo).relaciones }.getOrNull()
                    ?.also { relaciones[tipo] = it }
            }
        }.awaitAll()
        if (respuestas.any { it == null }) null else respuestas.filterNotNull().afinidades()
    }

    override suspend fun obtenerTecnica(id: String, habilidad: Boolean): Result<FichaTecnica> {
        val clave = id to habilidad
        tecnicas[clave]?.let { return Result.success(it) }
        return intentarApi {
            val dto = if (habilidad) api.habilidad(id) else api.movimiento(id)
            dto.aModelo().also { tecnicas[clave] = it }
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

private suspend fun <T> intentarApi(llamada: suspend () -> T): Result<T> = try {
    Result.success(llamada())
} catch (e: IOException) {
    Result.failure(e)
} catch (e: HttpException) {
    Result.failure(e)
} catch (e: IllegalArgumentException) {
    Result.failure(e)
}
