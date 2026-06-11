package com.krittapas.tictactoe.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.krittapas.tictactoe.R


val Outfit = FontFamily(
    Font(R.font.outfit_bold, FontWeight.Bold),
    Font(R.font.outfit_semibold, FontWeight.SemiBold),
    Font(R.font.outfit_regular, FontWeight.Normal),
    Font(R.font.outfit_black, FontWeight.Black),
    Font(R.font.outfit_thin, FontWeight.Thin),
    Font(R.font.outfit_light, FontWeight.Light),
    Font(R.font.outfit_medium, FontWeight.Medium),
    Font(R.font.outfit_extrabold, FontWeight.ExtraBold),
    Font(R.font.outfit_extralight, FontWeight.ExtraLight),
)
private val default = Typography()
// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge = default.displayLarge.copy(fontFamily = Outfit ),
    displayMedium = default.displayMedium.copy(fontFamily = Outfit),
    displaySmall = default.displaySmall.copy(fontFamily = Outfit),
    headlineLarge = default.headlineLarge.copy(fontFamily = Outfit),
    headlineMedium = default.headlineMedium.copy(fontFamily = Outfit),
    headlineSmall = default.headlineSmall.copy(fontFamily = Outfit),
    titleLarge = default.titleLarge.copy(fontFamily = Outfit),
    titleMedium = default.titleMedium.copy(fontFamily = Outfit),
    titleSmall = default.titleSmall.copy(fontFamily = Outfit),
    bodyLarge = default.bodyLarge.copy(fontFamily = Outfit),
    bodyMedium = default.bodyMedium.copy(fontFamily = Outfit),
    bodySmall = default.bodySmall.copy(fontFamily = Outfit),
    labelLarge = default.labelLarge.copy(fontFamily = Outfit),
    labelMedium = default.labelMedium.copy(fontFamily = Outfit),
    labelSmall = default.labelSmall.copy(fontFamily = Outfit),
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)