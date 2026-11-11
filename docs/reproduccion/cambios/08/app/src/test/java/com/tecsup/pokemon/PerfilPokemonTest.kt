package com.tecsup.pokemon

import com.tecsup.pokemon.data.mapper.afinidades
import com.tecsup.pokemon.data.remote.dto.RecursoDto
import com.tecsup.pokemon.data.remote.dto.RelacionesDto
import org.junit.Assert.assertEquals
import org.junit.Test

class PerfilPokemonTest {
    @Test fun aplicaDebilidadDobleResistenciaEInmunidadSinInventarTiposNeutros() {
        val planta = RelacionesDto(
            dobles = listOf(tipo("fire"), tipo("bug")),
            mitades = listOf(tipo("ground"), tipo("water")),
        )
        val segundoTipo = RelacionesDto(
            dobles = listOf(tipo("fire"), tipo("ground")),
            mitades = listOf(tipo("water")),
            inmunes = listOf(tipo("bug")),
        )
        assertEquals(mapOf("fire" to 4f, "water" to .25f, "bug" to 0f), listOf(planta, segundoTipo).afinidades())
    }

    private fun tipo(nombre: String) = RecursoDto(nombre, "https://pokeapi.co/api/v2/type/$nombre/")
}
