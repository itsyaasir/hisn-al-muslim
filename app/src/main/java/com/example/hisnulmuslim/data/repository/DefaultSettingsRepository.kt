package com.example.hisnulmuslim.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.hisnulmuslim.core.model.AppSettings
import com.example.hisnulmuslim.core.model.DefaultThemeSeedColor
import com.example.hisnulmuslim.core.model.ThemeMode
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    override fun observeSettings(): Flow<AppSettings> {
        return dataStore.data.map { preferences ->
            AppSettings(
                themeMode = preferences[THEME_MODE]?.let(ThemeMode::valueOf) ?: ThemeMode.SYSTEM,
                dynamicColorEnabled = preferences[DYNAMIC_COLOR] ?: true,
                pureBlackThemeEnabled = preferences[PURE_BLACK_THEME] ?: false,
                themeSeedColor = preferences[THEME_SEED_COLOR] ?: DefaultThemeSeedColor,
                fontScale = preferences[FONT_SCALE] ?: 1.0f,
                arabicFontScale = preferences[ARABIC_FONT_SCALE] ?: 1.15f,
                transliterationFontScale = preferences[TRANSLITERATION_FONT_SCALE] ?: 1.0f,
                translationFontScale = preferences[TRANSLATION_FONT_SCALE] ?: 1.0f,
                showTransliteration = preferences[SHOW_TRANSLITERATION] ?: true,
                showTranslation = preferences[SHOW_TRANSLATION] ?: true,
                showReference = preferences[SHOW_REFERENCE] ?: true,
                onboardingCompleted = preferences[ONBOARDING_COMPLETED] ?: false,
            )
        }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[THEME_MODE] = mode.name }
    }

    override suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { it[DYNAMIC_COLOR] = enabled }
    }

    override suspend fun setPureBlackTheme(enabled: Boolean) {
        dataStore.edit { it[PURE_BLACK_THEME] = enabled }
    }

    override suspend fun setThemeSeedColor(color: Long) {
        dataStore.edit { it[THEME_SEED_COLOR] = color }
    }

    override suspend fun setFontScale(scale: Float) {
        dataStore.edit { it[FONT_SCALE] = scale }
    }

    override suspend fun setArabicFontScale(scale: Float) {
        dataStore.edit { it[ARABIC_FONT_SCALE] = scale }
    }

    override suspend fun setTransliterationFontScale(scale: Float) {
        dataStore.edit { it[TRANSLITERATION_FONT_SCALE] = scale }
    }

    override suspend fun setTranslationFontScale(scale: Float) {
        dataStore.edit { it[TRANSLATION_FONT_SCALE] = scale }
    }

    override suspend fun setShowTransliteration(visible: Boolean) {
        dataStore.edit { it[SHOW_TRANSLITERATION] = visible }
    }

    override suspend fun setShowTranslation(visible: Boolean) {
        dataStore.edit { it[SHOW_TRANSLATION] = visible }
    }

    override suspend fun setShowReference(visible: Boolean) {
        dataStore.edit { it[SHOW_REFERENCE] = visible }
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { it[ONBOARDING_COMPLETED] = completed }
    }

    private companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val PURE_BLACK_THEME = booleanPreferencesKey("pure_black_theme")
        val THEME_SEED_COLOR = longPreferencesKey("theme_seed_color")
        val FONT_SCALE = floatPreferencesKey("font_scale")
        val ARABIC_FONT_SCALE = floatPreferencesKey("arabic_font_scale")
        val TRANSLITERATION_FONT_SCALE = floatPreferencesKey("transliteration_font_scale")
        val TRANSLATION_FONT_SCALE = floatPreferencesKey("translation_font_scale")
        val SHOW_TRANSLITERATION = booleanPreferencesKey("show_transliteration")
        val SHOW_TRANSLATION = booleanPreferencesKey("show_translation")
        val SHOW_REFERENCE = booleanPreferencesKey("show_reference")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }
}
