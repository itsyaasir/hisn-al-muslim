package com.example.hisnulmuslim.data.repository

import com.example.hisnulmuslim.core.model.AppSettings
import com.example.hisnulmuslim.core.model.ArabicFontFamily
import com.example.hisnulmuslim.core.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setDynamicColor(enabled: Boolean)
    suspend fun setPureBlackTheme(enabled: Boolean)
    suspend fun setThemeSeedColor(color: Long)
    suspend fun setFontScale(scale: Float)
    suspend fun setArabicFontFamily(fontFamily: ArabicFontFamily)
    suspend fun setArabicFontScale(scale: Float)
    suspend fun setTransliterationFontScale(scale: Float)
    suspend fun setTranslationFontScale(scale: Float)
    suspend fun setShowTransliteration(visible: Boolean)
    suspend fun setShowTranslation(visible: Boolean)
    suspend fun setShowReference(visible: Boolean)
    suspend fun setOnboardingCompleted(completed: Boolean)
}
