package com.yasir.hisnalmuslim.core.model

import androidx.compose.runtime.Immutable

@Immutable
data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColorEnabled: Boolean = true,
    val pureBlackThemeEnabled: Boolean = false,
    val themeSeedColor: Long = DefaultThemeSeedColor,
    val fontScale: Float = 1.0f,
    val collectionTitleLanguage: CollectionTitleLanguage = CollectionTitleLanguage.ENGLISH,
    val arabicFontFamily: ArabicFontFamily = ArabicFontFamily.AMIRI,
    val arabicFontScale: Float = 1.15f,
    val transliterationFontScale: Float = 1.0f,
    val translationFontScale: Float = 1.0f,
    val showTransliteration: Boolean = true,
    val showTranslation: Boolean = true,
    val showReference: Boolean = true,
    val notificationsEnabled: Boolean = false,
    val morningReminderEnabled: Boolean = false,
    val morningReminderMinutes: Int = 6 * 60,
    val morningReminderRingtoneUri: String? = null,
    val eveningReminderEnabled: Boolean = false,
    val eveningReminderMinutes: Int = 18 * 60,
    val eveningReminderRingtoneUri: String? = null,
    val sleepingReminderEnabled: Boolean = false,
    val sleepingReminderMinutes: Int = 22 * 60,
    val sleepingReminderRingtoneUri: String? = null,
)

enum class ArabicFontFamily {
    AMIRI,
    NOTO_NASKH,
    SCHEHERAZADE,
}

enum class CollectionTitleLanguage {
    ENGLISH,
    ARABIC,
}

const val DefaultThemeSeedColor: Long = 0xFFFFFFFFL
