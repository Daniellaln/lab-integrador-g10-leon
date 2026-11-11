package com.tecsup.pokemon.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Claro = lightColorScheme(
    primary = Bosque, onPrimary = Color.White,
    primaryContainer = Salvia, onPrimaryContainer = Tinta,
    secondary = Color(0xFF4B6556), onSecondary = Color.White,
    secondaryContainer = Color(0xFFE5EADD), onSecondaryContainer = Tinta,
    tertiary = Vino, onTertiary = Color.White,
    tertiaryContainer = Petalo, onTertiaryContainer = Color(0xFF542C3A),
    background = Marfil, onBackground = Tinta,
    surface = Marfil, onSurface = Tinta, surfaceContainer = Color(0xFFEDEFE4),
    surfaceContainerLow = Color(0xFFF1F1E7), surfaceContainerHigh = Color(0xFFE8EBDF),
    surfaceVariant = Color(0xFFE7EBDF), onSurfaceVariant = Color(0xFF4B5A50),
    outline = Color(0xFF67796B), outlineVariant = Color(0xFFCDD6C9),
    error = Color(0xFFA32A3E),
)

private val Oscuro = darkColorScheme(
    primary = BosqueNoche, onPrimary = Color(0xFF153621),
    primaryContainer = Color(0xFF284631), onPrimaryContainer = ClaroNoche,
    secondary = Color(0xFFBCD1BF), onSecondary = Color(0xFF21362A),
    secondaryContainer = Color(0xFF34493B), onSecondaryContainer = ClaroNoche,
    tertiary = Color(0xFFF4BDCC), onTertiary = Color(0xFF502536),
    tertiaryContainer = Color(0xFF4B303A), onTertiaryContainer = Color(0xFFFFDFE8),
    background = Noche, onBackground = ClaroNoche,
    surface = Noche, onSurface = ClaroNoche, surfaceContainer = Color(0xFF213126),
    surfaceContainerLow = Color(0xFF1E2A22), surfaceContainerHigh = Color(0xFF2B3B31),
    surfaceVariant = Color(0xFF304237), onSurfaceVariant = Color(0xFFBECBC0),
    outline = Color(0xFF8EA495), outlineVariant = Color(0xFF42574A),
)

@Composable
fun PokemonLeonTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) Oscuro else Claro,
        typography = TipografiaBotanica,
        content = content,
    )
}
