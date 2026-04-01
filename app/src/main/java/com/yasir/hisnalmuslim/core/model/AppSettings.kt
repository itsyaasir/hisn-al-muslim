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
    val repeatableReminderEnabled: Boolean = false,
    val repeatableReminderPattern: RepeatableReminderPattern = RepeatableReminderPattern.EVERY_4_HOURS,
    val repeatableReminderRingtoneUri: String? = null,
)

data class ReminderPreferences(
    val enabled: Boolean,
    val triggerMinutes: Int,
    val ringtoneUri: String?,
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

enum class RepeatableReminderPattern(
    val intervalMinutes: Int,
) {
    EVERY_HOUR(60),
    EVERY_2_HOURS(2 * 60),
    EVERY_4_HOURS(4 * 60),
    EVERY_6_HOURS(6 * 60),
    EVERY_8_HOURS(8 * 60),
    EVERY_12_HOURS(12 * 60),
}

const val DefaultThemeSeedColor: Long = 0xFFFFFFFFL

fun AppSettings.reminderPreferences(kind: ReminderKind): ReminderPreferences {
    return when (kind) {
        ReminderKind.MORNING -> ReminderPreferences(
            enabled = morningReminderEnabled,
            triggerMinutes = morningReminderMinutes,
            ringtoneUri = morningReminderRingtoneUri,
        )

        ReminderKind.EVENING -> ReminderPreferences(
            enabled = eveningReminderEnabled,
            triggerMinutes = eveningReminderMinutes,
            ringtoneUri = eveningReminderRingtoneUri,
        )

        ReminderKind.SLEEPING -> ReminderPreferences(
            enabled = sleepingReminderEnabled,
            triggerMinutes = sleepingReminderMinutes,
            ringtoneUri = sleepingReminderRingtoneUri,
        )

        ReminderKind.REPEATABLE -> ReminderPreferences(
            enabled = repeatableReminderEnabled,
            triggerMinutes = repeatableReminderPattern.intervalMinutes,
            ringtoneUri = repeatableReminderRingtoneUri,
        )
    }
}

fun AppSettings.shouldDeliverReminder(kind: ReminderKind): Boolean {
    return notificationsEnabled && reminderPreferences(kind).enabled
}
