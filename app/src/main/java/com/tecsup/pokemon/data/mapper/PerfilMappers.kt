package com.tecsup.pokemon.data.mapper

import com.tecsup.pokemon.data.remote.dto.*
import com.tecsup.pokemon.domain.model.EspeciePokemon
import com.tecsup.pokemon.domain.model.EtapaPokemon
import com.tecsup.pokemon.domain.model.FichaTecnica
import com.tecsup.pokemon.domain.model.nombreLegible

fun EspecieDto.aModelo() = EspeciePokemon(
    nombre = name,
    categoria = genera.firstOrNull { it.language.name == "es" }?.genus,
    descripcion = entradas.textoEnEspanol(),
    habitat = habitat?.name,
    captura = captura,
    legendario = legendario || mitico,
    generacion = generation?.name?.substringAfterLast('-')?.uppercase(),
)

fun TecnicaDto.aModelo() = FichaTecnica(
    nombre = names.firstOrNull { it.language.name == "es" }?.name ?: name.nombreLegible(),
    descripcion = entradas.textoEnEspanol(),
    tipo = type?.name,
    categoria = categoria?.name,
    potencia = power,
    precision = accuracy,
    pp = pp,
)

private fun List<TextoDto>.textoEnEspanol() = lastOrNull { it.language.name == "es" }
    ?.texto?.replace(Regex("[\\s\\u000c]+"), " ")?.trim()?.takeIf(String::isNotBlank)

fun EslabonDto.etapas(desde: String? = null): List<EtapaPokemon> = buildList {
    val numero = species.url.trimEnd('/').substringAfterLast('/').toIntOrNull()
    if (numero != null) {
        add(EtapaPokemon(species.name, numero, desde, requisitos.map { it.etiqueta() }.distinct()))
    }
    siguientes.forEach { addAll(it.etapas(species.name)) }
}

private fun CondicionDto.etiqueta(): String {
    val partes = buildList {
        nivel?.let { add("Nivel $it") }
        felicidad?.let { add("Amistad $it") }
        item?.name?.let {
            add(when (it) {
                "sun-stone" -> "Piedra Solar"
                "leaf-stone" -> "Piedra Hoja"
                "shiny-stone" -> "Piedra Día"
                else -> it.nombreLegible()
            })
        }
        if (trigger.name == "trade") add("Intercambio")
        if (momento == "day") add("De día")
        if (momento == "night") add("De noche")
    }
    return partes.joinToString(" · ").ifEmpty { "Condición especial" }
}

fun List<RelacionesDto>.afinidades(): Map<String, Float> {
    val factores = map { relacion ->
        buildMap {
            relacion.dobles.forEach { put(it.name, 2f) }
            relacion.mitades.forEach { put(it.name, .5f) }
            relacion.inmunes.forEach { put(it.name, 0f) }
        }
    }
    return factores.flatMap { it.keys }.distinct().associateWith { tipo ->
        factores.fold(1f) { total, factor -> total * (factor[tipo] ?: 1f) }
    }.filterValues { it != 1f }
}
