package com.yasir.hisnalmuslim.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yasir.hisnalmuslim.core.model.AppSettings
import com.yasir.hisnalmuslim.core.model.ArabicFontFamily
import com.yasir.hisnalmuslim.core.model.CollectionTitleLanguage
import com.yasir.hisnalmuslim.core.model.DefaultThemeSeedColor
import com.yasir.hisnalmuslim.core.model.ThemeMode
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    fun observeSettings(): Flow<AppSettings> {
        return dataStore.data.map { preferences ->
            AppSettings(
                themeMode = preferences[THEME_MODE]?.let(ThemeMode::valueOf) ?: ThemeMode.SYSTEM,
                dynamicColorEnabled = preferences[DYNAMIC_COLOR] ?: true,
                pureBlackThemeEnabled = preferences[PURE_BLACK_THEME] ?: false,
                themeSeedColor = preferences[THEME_SEED_COLOR] ?: DefaultThemeSeedColor,
                fontScale = preferences[FONT_SCALE] ?: 1.0f,
                collectionTitleLanguage = preferences[COLLECTION_TITLE_LANGUAGE]
                    ?.let(CollectionTitleLanguage::valueOf)
                    ?: CollectionTitleLanguage.ENGLISH,
                arabicFontFamily = preferences[ARABIC_FONT_FAMILY]?.let(ArabicFontFamily::valueOf)
                    ?: ArabicFontFamily.AMIRI,
                arabicFontScale = preferences[ARABIC_FONT_SCALE] ?: 1.15f,
                transliterationFontScale = preferences[TRANSLITERATION_FONT_SCALE] ?: 1.0f,
                translationFontScale = preferences[TRANSLATION_FONT_SCALE] ?: 1.0f,
                showTransliteration = preferences[SHOW_TRANSLITERATION] ?: true,
                showTranslation = preferences[SHOW_TRANSLATION] ?: true,
                showReference = preferences[SHOW_REFERENCE] ?: true,
                notificationsEnabled = preferences[NOTIFICATIONS_ENABLED] ?: false,
                morningReminderEnabled = preferences[MORNING_REMINDER_ENABLED] ?: false,
                morningReminderMinutes = preferences[MORNING_REMINDER_MINUTES] ?: 6 * 60,
                morningReminderRingtoneUri = preferences[MORNING_REMINDER_RINGTONE_URI],
                eveningReminderEnabled = preferences[EVENING_REMINDER_ENABLED] ?: false,
                eveningReminderMinutes = preferences[EVENING_REMINDER_MINUTES] ?: 18 * 60,
                eveningReminderRingtoneUri = preferences[EVENING_REMINDER_RINGTONE_URI],
                sleepingReminderEnabled = preferences[SLEEPING_REMINDER_ENABLED] ?: false,
                sleepingReminderMinutes = preferences[SLEEPING_REMINDER_MINUTES] ?: 22 * 60,
                sleepingReminderRingtoneUri = preferences[SLEEPING_REMINDER_RINGTONE_URI],
            )
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[THEME_MODE] = mode.name }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { it[DYNAMIC_COLOR] = enabled }
    }

    suspend fun setPureBlackTheme(enabled: Boolean) {
        dataStore.edit { it[PURE_BLACK_THEME] = enabled }
    }

    suspend fun setThemeSeedColor(color: Long) {
        dataStore.edit { it[THEME_SEED_COLOR] = color }
    }

    suspend fun setFontScale(scale: Float) {
        dataStore.edit { it[FONT_SCALE] = scale }
    }

    suspend fun setCollectionTitleLanguage(language: CollectionTitleLanguage) {
        dataStore.edit { it[COLLECTION_TITLE_LANGUAGE] = language.name }
    }

    suspend fun setArabicFontFamily(fontFamily: ArabicFontFamily) {
        dataStore.edit { it[ARABIC_FONT_FAMILY] = fontFamily.name }
    }

    suspend fun setArabicFontScale(scale: Float) {
        dataStore.edit { it[ARABIC_FONT_SCALE] = scale }
    }

    suspend fun setTransliterationFontScale(scale: Float) {
        dataStore.edit { it[TRANSLITERATION_FONT_SCALE] = scale }
    }

    suspend fun setTranslationFontScale(scale: Float) {
        dataStore.edit { it[TRANSLATION_FONT_SCALE] = scale }
    }

    suspend fun setShowTransliteration(visible: Boolean) {
        dataStore.edit { it[SHOW_TRANSLITERATION] = visible }
    }

    suspend fun setShowTranslation(visible: Boolean) {
        dataStore.edit { it[SHOW_TRANSLATION] = visible }
    }

    suspend fun setShowReference(visible: Boolean) {
        dataStore.edit { it[SHOW_REFERENCE] = visible }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setMorningReminderEnabled(enabled: Boolean) {
        dataStore.edit { it[MORNING_REMINDER_ENABLED] = enabled }
    }

    suspend fun setMorningReminderMinutes(minutes: Int) {
        dataStore.edit { it[MORNING_REMINDER_MINUTES] = minutes }
    }

    suspend fun setMorningReminderRingtoneUri(uri: String?) {
        dataStore.edit { preferences ->
            if (uri == null) {
                preferences.remove(MORNING_REMINDER_RINGTONE_URI)
            } else {
                preferences[MORNING_REMINDER_RINGTONE_URI] = uri
            }
        }
    }

    suspend fun setEveningReminderEnabled(enabled: Boolean) {
        dataStore.edit { it[EVENING_REMINDER_ENABLED] = enabled }
    }

    suspend fun setEveningReminderMinutes(minutes: Int) {
        dataStore.edit { it[EVENING_REMINDER_MINUTES] = minutes }
    }

    suspend fun setEveningReminderRingtoneUri(uri: String?) {
        dataStore.edit { preferences ->
            if (uri == null) {
                preferences.remove(EVENING_REMINDER_RINGTONE_URI)
            } else {
                preferences[EVENING_REMINDER_RINGTONE_URI] = uri
            }
        }
    }

    suspend fun setSleepingReminderEnabled(enabled: Boolean) {
        dataStore.edit { it[SLEEPING_REMINDER_ENABLED] = enabled }
    }

    suspend fun setSleepingReminderMinutes(minutes: Int) {
        dataStore.edit { it[SLEEPING_REMINDER_MINUTES] = minutes }
    }

    suspend fun setSleepingReminderRingtoneUri(uri: String?) {
        dataStore.edit { preferences ->
            if (uri == null) {
                preferences.remove(SLEEPING_REMINDER_RINGTONE_URI)
            } else {
                preferences[SLEEPING_REMINDER_RINGTONE_URI] = uri
            }
        }
    }

    fun observeNotificationPermissionPrompted(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[NOTIFICATION_PERMISSION_PROMPTED] ?: false
        }
    }

    suspend fun markNotificationPermissionPrompted() {
        dataStore.edit { it[NOTIFICATION_PERMISSION_PROMPTED] = true }
    }

    fun observeDailyReflectionOffset(): Flow<Long> {
        return dataStore.data.map { preferences ->
            preferences[DAILY_REFLECTION_OFFSET] ?: 0L
        }
    }

    suspend fun advanceDailyReflection() {
        dataStore.edit { preferences ->
            preferences[DAILY_REFLECTION_OFFSET] = (preferences[DAILY_REFLECTION_OFFSET] ?: 0L) + 1L
        }
    }

    private companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val PURE_BLACK_THEME = booleanPreferencesKey("pure_black_theme")
        val THEME_SEED_COLOR = longPreferencesKey("theme_seed_color")
        val FONT_SCALE = floatPreferencesKey("font_scale")
        val COLLECTION_TITLE_LANGUAGE = stringPreferencesKey("collection_title_language")
        val ARABIC_FONT_FAMILY = stringPreferencesKey("arabic_font_family")
        val ARABIC_FONT_SCALE = floatPreferencesKey("arabic_font_scale")
        val TRANSLITERATION_FONT_SCALE = floatPreferencesKey("transliteration_font_scale")
        val TRANSLATION_FONT_SCALE = floatPreferencesKey("translation_font_scale")
        val SHOW_TRANSLITERATION = booleanPreferencesKey("show_transliteration")
        val SHOW_TRANSLATION = booleanPreferencesKey("show_translation")
        val SHOW_REFERENCE = booleanPreferencesKey("show_reference")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val MORNING_REMINDER_ENABLED = booleanPreferencesKey("morning_reminder_enabled")
        val MORNING_REMINDER_MINUTES = intPreferencesKey("morning_reminder_minutes")
        val MORNING_REMINDER_RINGTONE_URI = stringPreferencesKey("morning_reminder_ringtone_uri")
        val EVENING_REMINDER_ENABLED = booleanPreferencesKey("evening_reminder_enabled")
        val EVENING_REMINDER_MINUTES = intPreferencesKey("evening_reminder_minutes")
        val EVENING_REMINDER_RINGTONE_URI = stringPreferencesKey("evening_reminder_ringtone_uri")
        val SLEEPING_REMINDER_ENABLED = booleanPreferencesKey("sleeping_reminder_enabled")
        val SLEEPING_REMINDER_MINUTES = intPreferencesKey("sleeping_reminder_minutes")
        val SLEEPING_REMINDER_RINGTONE_URI = stringPreferencesKey("sleeping_reminder_ringtone_uri")
        val NOTIFICATION_PERMISSION_PROMPTED = booleanPreferencesKey("notification_permission_prompted")
        val DAILY_REFLECTION_OFFSET = longPreferencesKey("daily_reflection_offset")
    }
}
