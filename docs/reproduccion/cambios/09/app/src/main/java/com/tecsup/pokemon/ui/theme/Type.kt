package com.tecsup.pokemon.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.tecsup.pokemon.R

private val Manrope = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold),
)

private fun texto(tamano: Int, linea: Int, peso: FontWeight = FontWeight.Normal) =
    TextStyle(fontFamily = Manrope, fontSize = tamano.sp, lineHeight = linea.sp, fontWeight = peso)

internal val TipografiaBotanica = Typography(
    displaySmall = texto(43, 48, FontWeight.Bold).copy(letterSpacing = (-1.9).sp),
    headlineLarge = texto(34, 39, FontWeight.Bold).copy(letterSpacing = (-1.2).sp),
    headlineMedium = texto(28, 34, FontWeight.Bold).copy(letterSpacing = (-.8).sp),
    headlineSmall = texto(23, 29, FontWeight.Bold).copy(letterSpacing = (-.5).sp),
    titleLarge = texto(21, 27, FontWeight.Bold),
    titleMedium = texto(16, 22, FontWeight.Bold),
    titleSmall = texto(14, 20, FontWeight.Bold),
    bodyLarge = texto(16, 24),
    bodyMedium = texto(14, 21),
    bodySmall = texto(12, 17),
    labelLarge = texto(14, 20, FontWeight.Bold),
    labelMedium = texto(12, 16, FontWeight.SemiBold),
    labelSmall = texto(11, 15, FontWeight.SemiBold),
)
