package com.yasir.hisnalmuslim.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.Immutable
import com.yasir.hisnalmuslim.core.model.AppSettings
import com.yasir.hisnalmuslim.core.model.Dhikr
import com.yasir.hisnalmuslim.data.repository.DhikrRepository
import com.yasir.hisnalmuslim.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Immutable
data class FavoritesUiState(
    val favorites: List<Dhikr> = emptyList(),
    val settings: AppSettings = AppSettings(),
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: DhikrRepository,
    settingsRepository: SettingsRepository,
) : ViewModel() {
    val uiState: StateFlow<FavoritesUiState> = combine(
        repository.observeFavorites(),
        settingsRepository.observeSettings(),
    ) { favorites, settings ->
        FavoritesUiState(
            favorites = favorites,
            settings = settings,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FavoritesUiState(),
        )

    fun removeFavorite(dhikrId: Long) {
        viewModelScope.launch {
            repository.removeFavorite(dhikrId)
        }
    }

    fun restoreFavorite(dhikrId: Long) {
        viewModelScope.launch {
            repository.addFavorite(dhikrId)
        }
    }
}
