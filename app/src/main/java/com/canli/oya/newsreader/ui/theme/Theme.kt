package com.canli.oya.newsreader.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Red200,
    primaryVariant = Red700,
    onPrimary = Color.Black,
    secondary = Red200,
    onSecondary = Color.Black,
    error = Red200
)

private val LightColorPalette = lightColors(
    primary = Red500,
    primaryVariant = Red700,
    onPrimary = Color.White,
    secondary = Red700,
    secondaryVariant = Red900,
    onSecondary = Color.White,
    error = Red800

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun NewsReaderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = NewsTypography,
        shapes = Shapes,
        content = content
    )
}
