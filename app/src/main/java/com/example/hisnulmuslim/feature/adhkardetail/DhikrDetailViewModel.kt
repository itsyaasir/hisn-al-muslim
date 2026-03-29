package com.example.hisnulmuslim.feature.adhkardetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hisnulmuslim.core.model.AppSettings
import com.example.hisnulmuslim.core.model.Dhikr
import com.example.hisnulmuslim.core.model.DhikrProgress
import com.example.hisnulmuslim.data.repository.DhikrRepository
import com.example.hisnulmuslim.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DhikrDetailUiState(
    val dhikr: Dhikr? = null,
    val isFavorite: Boolean = false,
    val progress: DhikrProgress? = null,
    val settings: AppSettings = AppSettings(),
    val previousDhikrId: Long? = null,
    val nextDhikrId: Long? = null,
) {
    val currentCount: Int get() = progress?.currentCount ?: 0
    val completedCount: Int get() = progress?.completedCount ?: 0
    val repeatTarget: Int? get() = dhikr?.repeatCount
    val showsCounterHero: Boolean get() = repeatTarget?.let { it > 1 } == true
    val remainingCount: Int?
        get() {
            val target = repeatTarget?.takeIf { it > 1 } ?: return null
            return (target - currentCount).coerceAtLeast(0)
        }
    val isCounterRoundComplete: Boolean
        get() {
            val target = repeatTarget?.takeIf { it > 1 } ?: return false
            return currentCount >= target
        }
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DhikrDetailViewModel @Inject constructor(
    private val repository: DhikrRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val dhikrId = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<DhikrDetailUiState> = dhikrId
        .flatMapLatest { currentDhikrId ->
            if (currentDhikrId == null) {
                flowOf(DhikrDetailUiState())
            } else {
                combine(
                    repository.observeDhikrDetail(currentDhikrId),
                    repository.observeIsFavorite(currentDhikrId),
                    repository.observeProgress(currentDhikrId),
                    settingsRepository.observeSettings(),
                    repository.observeAllDhikrOrdered(),
                ) { dhikr, isFavorite, progress, settings, orderedDhikr ->
                    val currentIndex = orderedDhikr.indexOfFirst { it.id == currentDhikrId }
                    DhikrDetailUiState(
                        dhikr = dhikr,
                        isFavorite = isFavorite,
                        progress = progress,
                        settings = settings,
                        previousDhikrId = orderedDhikr.getOrNull(currentIndex - 1)?.id,
                        nextDhikrId = orderedDhikr.getOrNull(currentIndex + 1)?.id,
                    )
                }
            }
        }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DhikrDetailUiState(),
    )

    fun bind(dhikrId: Long) {
        if (this.dhikrId.value == dhikrId) return
        this.dhikrId.value = dhikrId
    }

    fun toggleFavorite() {
        val dhikrId = dhikrId.value ?: return
        viewModelScope.launch {
            repository.toggleFavorite(dhikrId)
        }
    }

    fun incrementCounter() {
        val state = uiState.value
        val repeatTarget = state.repeatTarget?.takeIf { it > 1 } ?: return
        if (state.currentCount >= repeatTarget) return

        val nextCount = state.currentCount + 1
        val updatedCurrentCount = nextCount.coerceAtMost(repeatTarget)
        val updatedCompletedCount = if (
            updatedCurrentCount == repeatTarget &&
            state.currentCount < repeatTarget
        ) {
            state.completedCount + 1
        } else {
            state.completedCount
        }

        val dhikrId = dhikrId.value ?: return
        viewModelScope.launch {
            repository.updateProgress(
                dhikrId = dhikrId,
                currentCount = updatedCurrentCount,
                completedCount = updatedCompletedCount,
            )
        }
    }

    fun resetCounter() {
        val state = uiState.value
        if (!state.showsCounterHero) return
        if (state.currentCount == 0 && state.completedCount == 0) return
        val dhikrId = dhikrId.value ?: return
        viewModelScope.launch {
            repository.updateProgress(
                dhikrId = dhikrId,
                currentCount = 0,
                completedCount = 0,
            )
        }
    }
}
