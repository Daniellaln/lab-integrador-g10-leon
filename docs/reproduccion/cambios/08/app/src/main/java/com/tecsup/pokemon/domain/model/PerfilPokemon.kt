package com.tecsup.pokemon.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PerfilPokemon(
    val estadisticas: Map<String, Int>,
    val habilidades: List<HabilidadPokemon>,
    val movimientos: List<String>,
    val especie: EspeciePokemon? = null,
    val evolucion: List<EtapaPokemon>? = null,
    val afinidades: Map<String, Float>? = null,
) {
    val completo get() = especie != null && evolucion != null && afinidades != null
}

@Serializable
data class HabilidadPokemon(val id: String, val oculta: Boolean)

@Serializable
data class EspeciePokemon(
    val nombre: String,
    val categoria: String?,
    val descripcion: String?,
    val habitat: String?,
    val captura: Int,
    val legendario: Boolean,
    val generacion: String? = null,
)

@Serializable
data class EtapaPokemon(val nombre: String, val numero: Int, val desde: String?, val condiciones: List<String>)

data class FichaTecnica(
    val nombre: String,
    val descripcion: String?,
    val tipo: String? = null,
    val categoria: String? = null,
    val potencia: Int? = null,
    val precision: Int? = null,
    val pp: Int? = null,
)

val EstadisticasPokemon: Map<String, String> = linkedMapOf(
    "hp" to "PS",
    "attack" to "Ataque",
    "defense" to "Defensa",
    "special-attack" to "Ataque especial",
    "special-defense" to "Defensa especial",
    "speed" to "Velocidad",
)

fun String.nombreLegible() = replace('-', ' ').replaceFirstChar(Char::uppercaseChar)
