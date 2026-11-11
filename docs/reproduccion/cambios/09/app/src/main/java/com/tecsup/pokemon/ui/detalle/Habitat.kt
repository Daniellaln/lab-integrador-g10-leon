package com.tecsup.pokemon.ui.detalle

import androidx.annotation.DrawableRes
import com.tecsup.pokemon.R
import com.tecsup.pokemon.domain.model.Item

internal data class Habitat(val nombre: String, @param:DrawableRes val fondo: Int, @param:DrawableRes val borde: Int)

internal val Habitats = mapOf(
    "forest" to Habitat("Bosque", R.drawable.hab_bosque, R.drawable.borde_bosque),
    "grassland" to Habitat("Pradera", R.drawable.hab_pradera, R.drawable.borde_pradera),
    "cave" to Habitat("Cueva", R.drawable.hab_cueva, R.drawable.borde_cueva),
    "mountain" to Habitat("Montaña", R.drawable.hab_montana, R.drawable.borde_montana),
    "rare" to Habitat("Santuario", R.drawable.hab_santuario, R.drawable.borde_santuario),
    "rough-terrain" to Habitat("Terreno rocoso", R.drawable.hab_rocoso, R.drawable.borde_rocoso),
    "sea" to Habitat("Mar", R.drawable.hab_mar, R.drawable.borde_mar),
    "urban" to Habitat("Jardín urbano", R.drawable.hab_urbano, R.drawable.borde_urbano),
    "waters-edge" to Habitat("Ribera", R.drawable.hab_ribera, R.drawable.borde_ribera),
    "invernadero" to Habitat("Invernadero", R.drawable.hab_invernadero, R.drawable.borde_invernadero),
)

internal fun Item.habitatVisual(): Pair<Habitat, Boolean> {
    val especie = perfil?.especie
    val oficial = especie?.habitat?.let(Habitats::get)
    if (oficial != null) return oficial to true
    val sugerido = when {
        especie == null -> "invernadero"
        especie.nombre in setOf("cacnea", "cacturne", "bramblin", "brambleghast") -> "rough-terrain"
        especie.nombre == "dhelmise" -> "sea"
        especie.legendario -> "rare"
        "water" in tipos -> "waters-edge"
        "ice" in tipos -> "mountain"
        "ground" in tipos || "rock" in tipos -> "rough-terrain"
        "ghost" in tipos -> "cave"
        "steel" in tipos || "electric" in tipos -> "urban"
        "fairy" in tipos -> "rare"
        tipos == listOf("grass") -> "grassland"
        else -> "forest"
    }
    return Habitats.getValue(sugerido) to false
}
