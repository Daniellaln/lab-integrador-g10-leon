package com.tecsup.pokemon.data.remote

import com.tecsup.pokemon.data.remote.dto.ItemDto
import com.tecsup.pokemon.data.remote.dto.ListaTipoDto
import com.tecsup.pokemon.data.remote.dto.EspecieDto
import com.tecsup.pokemon.data.remote.dto.EvolucionDto
import com.tecsup.pokemon.data.remote.dto.TecnicaDto
import com.tecsup.pokemon.data.remote.dto.TipoDetalleDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("type/grass")
    suspend fun listar(): ListaTipoDto

    @GET("pokemon/{nombre}")
    suspend fun detalle(@Path("nombre") nombre: String): ItemDto

    @GET("pokemon-species/{nombre}")
    suspend fun especie(@Path("nombre") nombre: String): EspecieDto

    @GET("evolution-chain/{id}")
    suspend fun evolucion(@Path("id") id: String): EvolucionDto

    @GET("type/{nombre}")
    suspend fun tipo(@Path("nombre") nombre: String): TipoDetalleDto

    @GET("move/{nombre}")
    suspend fun movimiento(@Path("nombre") nombre: String): TecnicaDto

    @GET("ability/{nombre}")
    suspend fun habilidad(@Path("nombre") nombre: String): TecnicaDto
}
