package com.tecsup.pokemon.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RutaDto(val url: String)

@Serializable
data class EstadisticaDto(@SerialName("base_stat") val valor: Int, val stat: RecursoDto)

@Serializable
data class HabilidadDto(val ability: RecursoDto?, @SerialName("is_hidden") val oculta: Boolean)

@Serializable
data class MovimientoAprendidoDto(val move: RecursoDto)

@Serializable
data class TextoDto(@SerialName("flavor_text") val texto: String, val language: RecursoDto)

@Serializable
data class NombreDto(val name: String, val language: RecursoDto)

@Serializable
data class CategoriaDto(val genus: String, val language: RecursoDto)

@Serializable
data class EspecieDto(
    val name: String,
    val genera: List<CategoriaDto> = emptyList(),
    @SerialName("flavor_text_entries") val entradas: List<TextoDto> = emptyList(),
    val habitat: RecursoDto? = null,
    @SerialName("capture_rate") val captura: Int = 0,
    @SerialName("is_legendary") val legendario: Boolean = false,
    @SerialName("is_mythical") val mitico: Boolean = false,
    @SerialName("evolution_chain") val evolucion: RutaDto? = null,
    val generation: RecursoDto? = null,
)

@Serializable
data class CondicionDto(
    val trigger: RecursoDto,
    val item: RecursoDto? = null,
    @SerialName("min_level") val nivel: Int? = null,
    @SerialName("min_happiness") val felicidad: Int? = null,
    @SerialName("time_of_day") val momento: String = "",
)

@Serializable
data class EslabonDto(
    val species: RecursoDto,
    @SerialName("evolution_details") val requisitos: List<CondicionDto> = emptyList(),
    @SerialName("evolves_to") val siguientes: List<EslabonDto> = emptyList(),
)

@Serializable
data class EvolucionDto(val chain: EslabonDto)

@Serializable
data class RelacionesDto(
    @SerialName("double_damage_from") val dobles: List<RecursoDto> = emptyList(),
    @SerialName("half_damage_from") val mitades: List<RecursoDto> = emptyList(),
    @SerialName("no_damage_from") val inmunes: List<RecursoDto> = emptyList(),
)

@Serializable
data class TipoDetalleDto(@SerialName("damage_relations") val relaciones: RelacionesDto)

@Serializable
data class TecnicaDto(
    val name: String,
    val names: List<NombreDto> = emptyList(),
    @SerialName("flavor_text_entries") val entradas: List<TextoDto> = emptyList(),
    val type: RecursoDto? = null,
    @SerialName("damage_class") val categoria: RecursoDto? = null,
    val power: Int? = null,
    val accuracy: Int? = null,
    val pp: Int? = null,
)
