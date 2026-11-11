package com.tecsup.pokemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.lista.ListaScreen
import com.tecsup.pokemon.ui.theme.PokemonLeonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokemonLeonTheme { Demo() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Demo() {
    val ejemplos = listOf(Item("bulbasaur", 1, null), Item("bellossom", 182, null), Item("lilligant", 549, null))
    Scaffold(topBar = { TopAppBar(title = { Text("Datos de prueba") }) }) { padding ->
        ListaScreen(UiState.Exito(ejemplos), Modifier.padding(padding))
    }
}
