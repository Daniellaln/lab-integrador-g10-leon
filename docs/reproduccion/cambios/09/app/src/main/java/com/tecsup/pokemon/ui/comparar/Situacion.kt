package com.tecsup.pokemon.ui.comparar

import com.tecsup.pokemon.domain.model.Item

internal enum class Situacion(val titulo: String, val criterio: String) {
    INICIATIVA("Atacar primero", "Velocidad base"),
    ATAQUE("Presión ofensiva", "Mayor entre ataque físico y especial"),
    AGUANTE("Aguantar golpes", "Suma de PS, Defensa y Defensa especial"),
    VERSATILIDAD("Resistir más tipos", "Cantidad de resistencias e inmunidades"),
    DUELO("Cruce de tipos", "Mejor efectividad de un tipo propio contra el rival");

    fun valor(item: Item, rival: Item): Float? {
        val perfil = item.perfil ?: return null
        val stats = perfil.estadisticas
        return when (this) {
            INICIATIVA -> stats["speed"]?.toFloat()
            ATAQUE -> listOfNotNull(stats["attack"], stats["special-attack"]).maxOrNull()?.toFloat()
            AGUANTE -> {
                val atributos = listOfNotNull(stats["hp"], stats["defense"], stats["special-defense"])
                if (atributos.size == 3) atributos.sum().toFloat() else null
            }
            VERSATILIDAD -> perfil.afinidades?.count { it.value < 1f }?.toFloat()
            DUELO -> rival.perfil?.afinidades?.let { tabla ->
                item.tipos.maxOfOrNull { tabla[it] ?: 1f }
            }
        }
    }
}
