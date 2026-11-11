package com.tecsup.pokemon.data.remote

import com.tecsup.pokemon.data.remote.dto.ItemDto
import com.tecsup.pokemon.data.remote.dto.ListaTipoDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("type/grass")
    suspend fun listar(): ListaTipoDto

    @GET("pokemon/{nombre}")
    suspend fun detalle(@Path("nombre") nombre: String): ItemDto
}
