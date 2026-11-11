package com.tecsup.pokemon.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecursoDto(val name: String, val url: String)

@Serializable
data class EntradaTipoDto(val pokemon: RecursoDto)

@Serializable
data class ListaTipoDto(val pokemon: List<EntradaTipoDto>)

@Serializable
data class TipoDto(val type: RecursoDto)

@Serializable
data class ImagenDto(@SerialName("front_default") val frente: String? = null)

@Serializable
data class OtrasImagenesDto(@SerialName("official-artwork") val arte: ImagenDto? = null)

@Serializable
data class SpritesDto(
    @SerialName("front_default") val frente: String? = null,
    val other: OtrasImagenesDto? = null,
)

@Serializable
data class ItemDto(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<TipoDto>,
    val sprites: SpritesDto,
    val species: RecursoDto? = null,
    val stats: List<EstadisticaDto> = emptyList(),
    val abilities: List<HabilidadDto> = emptyList(),
    val moves: List<MovimientoAprendidoDto> = emptyList(),
)
