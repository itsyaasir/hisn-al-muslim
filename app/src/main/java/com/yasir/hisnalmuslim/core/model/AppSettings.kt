package com.yasir.hisnalmuslim.core.model

import androidx.compose.runtime.Immutable

@Immutable
data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColorEnabled: Boolean = true,
    val pureBlackThemeEnabled: Boolean = false,
    val themeSeedColor: Long = DefaultThemeSeedColor,
    val fontScale: Float = 1.0f,
    val arabicFontFamily: ArabicFontFamily = ArabicFontFamily.AMIRI,
    val arabicFontScale: Float = 1.15f,
    val transliterationFontScale: Float = 1.0f,
    val translationFontScale: Float = 1.0f,
    val showTransliteration: Boolean = true,
    val showTranslation: Boolean = true,
    val showReference: Boolean = true,
)

enum class ArabicFontFamily {
    AMIRI,
    NOTO_NASKH,
    SCHEHERAZADE,
}

const val DefaultThemeSeedColor: Long = 0xFFFFFFFFL
