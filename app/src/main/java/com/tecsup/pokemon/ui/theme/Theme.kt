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
    secondary = Color(0xFF1B6272), onSecondary = Color.White,
    secondaryContainer = Color(0xFFE4F6F7), onSecondaryContainer = Tinta,
    tertiary = Vino, onTertiary = Color.White,
    tertiaryContainer = Petalo, onTertiaryContainer = Color(0xFF3E216D),
    background = Marfil, onBackground = Tinta,
    surface = Color.White, onSurface = Tinta, surfaceContainer = Color(0xFFF1F7F5),
    surfaceContainerLow = Color(0xFFF8FBFA), surfaceContainerHigh = Color(0xFFE8F4EF),
    surfaceVariant = Color(0xFFE3EFEB), onSurfaceVariant = Color(0xFF445A53),
    outline = Color(0xFF627B72), outlineVariant = Color(0xFFC8DCD5),
    error = Color(0xFFA32A3E),
)

private val Oscuro = darkColorScheme(
    primary = BosqueNoche, onPrimary = Color(0xFF153621),
    primaryContainer = Color(0xFF284631), onPrimaryContainer = ClaroNoche,
    secondary = Color(0xFFBCD1BF), onSecondary = Color(0xFF21362A),
    secondaryContainer = Color(0xFF34493B), onSecondaryContainer = ClaroNoche,
    tertiary = Color(0xFFD6BCFF), onTertiary = Color(0xFF38215D),
    tertiaryContainer = Color(0xFF382B52), onTertiaryContainer = Color(0xFFF2E6FF),
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
