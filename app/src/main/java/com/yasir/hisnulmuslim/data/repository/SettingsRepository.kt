package com.yasir.hisnulmuslim.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yasir.hisnulmuslim.core.model.AppSettings
import com.yasir.hisnulmuslim.core.model.ArabicFontFamily
import com.yasir.hisnulmuslim.core.model.DefaultThemeSeedColor
import com.yasir.hisnulmuslim.core.model.ThemeMode
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
                arabicFontFamily = preferences[ARABIC_FONT_FAMILY]?.let(ArabicFontFamily::valueOf)
                    ?: ArabicFontFamily.AMIRI,
                arabicFontScale = preferences[ARABIC_FONT_SCALE] ?: 1.15f,
                transliterationFontScale = preferences[TRANSLITERATION_FONT_SCALE] ?: 1.0f,
                translationFontScale = preferences[TRANSLATION_FONT_SCALE] ?: 1.0f,
                showTransliteration = preferences[SHOW_TRANSLITERATION] ?: true,
                showTranslation = preferences[SHOW_TRANSLATION] ?: true,
                showReference = preferences[SHOW_REFERENCE] ?: true,
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
    private companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val PURE_BLACK_THEME = booleanPreferencesKey("pure_black_theme")
        val THEME_SEED_COLOR = longPreferencesKey("theme_seed_color")
        val FONT_SCALE = floatPreferencesKey("font_scale")
        val ARABIC_FONT_FAMILY = stringPreferencesKey("arabic_font_family")
        val ARABIC_FONT_SCALE = floatPreferencesKey("arabic_font_scale")
        val TRANSLITERATION_FONT_SCALE = floatPreferencesKey("transliteration_font_scale")
        val TRANSLATION_FONT_SCALE = floatPreferencesKey("translation_font_scale")
        val SHOW_TRANSLITERATION = booleanPreferencesKey("show_transliteration")
        val SHOW_TRANSLATION = booleanPreferencesKey("show_translation")
        val SHOW_REFERENCE = booleanPreferencesKey("show_reference")
    }
}
