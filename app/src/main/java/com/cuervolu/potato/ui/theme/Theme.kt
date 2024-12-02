package com.cuervolu.potato.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkBurgundy,
    onPrimary = PastelPeach,
    primaryContainer = BrightCoral,
    onPrimaryContainer = PastelPeach,
    secondary = LightCoral,
    onSecondary = DarkBurgundy,
    secondaryContainer = BrightCoral,
    onSecondaryContainer = PastelPeach,
    tertiary = PastelPeach,
    onTertiary = DarkBurgundy,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = SoftBurgundy,
    onPrimary = LightPeach,
    primaryContainer = SoftCoral,
    onPrimaryContainer = SoftBurgundy,
    secondary = SoftRed,
    onSecondary = LightPeach,
    secondaryContainer = SoftCoral,
    onSecondaryContainer = SoftBurgundy,
    tertiary = LightPeach,
    onTertiary = SoftBurgundy,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight
)

@Composable
fun PotatoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}