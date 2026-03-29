package com.example.hisnulmuslim.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hisnulmuslim.core.model.AppSettings
import com.example.hisnulmuslim.core.model.DefaultThemeSeedColor
import com.example.hisnulmuslim.core.model.ThemeMode
import com.example.hisnulmuslim.data.repository.DhikrRepository
import com.example.hisnulmuslim.data.repository.SettingsRepository
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

    fun resetProgress() {
        viewModelScope.launch { dhikrRepository.resetProgress() }
    }
}
