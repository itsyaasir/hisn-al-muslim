package com.example.hisnulmuslim.core.model

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColorEnabled: Boolean = true,
    val pureBlackThemeEnabled: Boolean = false,
    val themeSeedColor: Long = DefaultThemeSeedColor,
    val fontScale: Float = 1.0f,
    val arabicFontScale: Float = 1.15f,
    val transliterationFontScale: Float = 1.0f,
    val translationFontScale: Float = 1.0f,
    val showTransliteration: Boolean = true,
    val showTranslation: Boolean = true,
    val showReference: Boolean = true,
    val onboardingCompleted: Boolean = false,
)

const val DefaultThemeSeedColor: Long = 0xFFFFFFFFL
