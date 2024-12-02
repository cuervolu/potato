package com.cuervolu.potato.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.cuervolu.potato.data.preferences.UserPreferencesManager

@Composable
fun rememberThemeState(
    preferencesManager: UserPreferencesManager
): Boolean {
    val themeMode by preferencesManager.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    val systemIsDark = isSystemInDarkTheme()

    return when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemIsDark
    }
}