package com.cuervolu.potato.ui.theme

import androidx.annotation.StringRes
import com.cuervolu.potato.R

enum class ThemeMode(@StringRes val titleResId: Int) {
    LIGHT(R.string.light_theme),
    DARK(R.string.dark_theme),
    SYSTEM(R.string.system_theme)
}