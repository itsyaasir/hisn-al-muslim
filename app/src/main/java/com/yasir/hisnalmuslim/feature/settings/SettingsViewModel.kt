package com.yasir.hisnalmuslim.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yasir.hisnalmuslim.core.model.AppSettings
import com.yasir.hisnalmuslim.core.model.ArabicFontFamily
import com.yasir.hisnalmuslim.core.model.CollectionTitleLanguage
import com.yasir.hisnalmuslim.core.model.DefaultThemeSeedColor
import com.yasir.hisnalmuslim.core.model.ThemeMode
import com.yasir.hisnalmuslim.data.repository.DhikrRepository
import com.yasir.hisnalmuslim.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val dhikrRepository: DhikrRepository,
) : ViewModel() {
    val uiState: StateFlow<AppSettings> = repository.observeSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings(),
        )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { repository.setThemeMode(mode) }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            repository.setDynamicColor(enabled)
            if (enabled) {
                repository.setThemeSeedColor(DefaultThemeSeedColor)
            }
        }
    }

    fun setPureBlackTheme(enabled: Boolean) {
        viewModelScope.launch { repository.setPureBlackTheme(enabled) }
    }

    fun setThemeSeedColor(color: Long) {
        viewModelScope.launch {
            repository.setDynamicColor(false)
            repository.setThemeSeedColor(color)
        }
    }

    fun setFontScale(scale: Float) {
        viewModelScope.launch { repository.setFontScale(scale) }
    }

    fun setCollectionTitleLanguage(language: CollectionTitleLanguage) {
        viewModelScope.launch { repository.setCollectionTitleLanguage(language) }
    }

    fun setArabicFontFamily(fontFamily: ArabicFontFamily) {
        viewModelScope.launch { repository.setArabicFontFamily(fontFamily) }
    }

    fun setArabicFontScale(scale: Float) {
        viewModelScope.launch { repository.setArabicFontScale(scale) }
    }

    fun setTransliterationFontScale(scale: Float) {
        viewModelScope.launch { repository.setTransliterationFontScale(scale) }
    }

    fun setTranslationFontScale(scale: Float) {
        viewModelScope.launch { repository.setTranslationFontScale(scale) }
    }

    fun setShowTransliteration(visible: Boolean) {
        viewModelScope.launch { repository.setShowTransliteration(visible) }
    }

    fun setShowTranslation(visible: Boolean) {
        viewModelScope.launch { repository.setShowTranslation(visible) }
    }

    fun setShowReference(visible: Boolean) {
        viewModelScope.launch { repository.setShowReference(visible) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setNotificationsEnabled(enabled) }
    }

    fun setMorningReminderEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setMorningReminderEnabled(enabled) }
    }

    fun setMorningReminderMinutes(minutes: Int) {
        viewModelScope.launch { repository.setMorningReminderMinutes(minutes) }
    }

    fun setMorningReminderRingtoneUri(uri: String?) {
        viewModelScope.launch { repository.setMorningReminderRingtoneUri(uri) }
    }

    fun setEveningReminderEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setEveningReminderEnabled(enabled) }
    }

    fun setEveningReminderMinutes(minutes: Int) {
        viewModelScope.launch { repository.setEveningReminderMinutes(minutes) }
    }

    fun setEveningReminderRingtoneUri(uri: String?) {
        viewModelScope.launch { repository.setEveningReminderRingtoneUri(uri) }
    }

    fun setSleepingReminderEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setSleepingReminderEnabled(enabled) }
    }

    fun setSleepingReminderMinutes(minutes: Int) {
        viewModelScope.launch { repository.setSleepingReminderMinutes(minutes) }
    }

    fun setSleepingReminderRingtoneUri(uri: String?) {
        viewModelScope.launch { repository.setSleepingReminderRingtoneUri(uri) }
    }

    fun clearFavorites() {
        viewModelScope.launch { dhikrRepository.clearFavorites() }
    }
}
