package com.yasir.hisnalmuslim.feature.adhkardetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.Immutable
import com.yasir.hisnalmuslim.core.model.AppSettings
import com.yasir.hisnalmuslim.core.model.Dhikr
import com.yasir.hisnalmuslim.core.model.DhikrProgress
import com.yasir.hisnalmuslim.data.repository.DhikrRepository
import com.yasir.hisnalmuslim.data.repository.SettingsRepository
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

@Immutable
data class DhikrDetailUiState(
    val collectionDhikr: List<Dhikr> = emptyList(),
    val currentIndex: Int = 0,
    val isFavorite: Boolean = false,
    val progress: DhikrProgress? = null,
    val settings: AppSettings = AppSettings(),
) {
    val dhikr: Dhikr? get() = collectionDhikr.getOrNull(currentIndex)
    val currentDhikrId: Long? get() = dhikr?.id
    val showsCollectionDots: Boolean get() = collectionDhikr.size > 1
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
    settingsRepository: SettingsRepository,
) : ViewModel() {
    private val dhikrId = MutableStateFlow<Long?>(null)
    private val collectionId = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<DhikrDetailUiState> = combine(
        collectionId.flatMapLatest { currentCollectionId ->
            if (currentCollectionId == null) {
                flowOf(emptyList())
            } else {
                repository.observeCollectionDhikr(currentCollectionId)
            }
        },
        dhikrId,
        dhikrId.flatMapLatest { currentDhikrId ->
            if (currentDhikrId == null) flowOf(false) else repository.observeIsFavorite(currentDhikrId)
        },
        dhikrId.flatMapLatest { currentDhikrId ->
            if (currentDhikrId == null) flowOf(null) else repository.observeProgress(currentDhikrId)
        },
        settingsRepository.observeSettings(),
    ) { collectionDhikr, currentDhikrId, isFavorite, progress, settings ->
        val currentIndex = collectionDhikr.indexOfFirst { it.id == currentDhikrId }
            .takeIf { it >= 0 }
            ?: 0
        DhikrDetailUiState(
            collectionDhikr = collectionDhikr,
            currentIndex = currentIndex,
            isFavorite = isFavorite,
            progress = progress,
            settings = settings,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DhikrDetailUiState(),
    )

    fun bind(dhikrId: Long, collectionId: Long) {
        if (this.dhikrId.value == dhikrId && this.collectionId.value == collectionId) return
        this.collectionId.value = collectionId
        this.dhikrId.value = dhikrId
    }

    fun selectDhikr(dhikrId: Long) {
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

        val dhikrId = state.currentDhikrId ?: return
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
        val dhikrId = state.currentDhikrId ?: return
        viewModelScope.launch {
            repository.updateProgress(
                dhikrId = dhikrId,
                currentCount = 0,
                completedCount = 0,
            )
        }
    }

    fun clearCollectionProgress(collectionId: Long) {
        viewModelScope.launch {
            repository.clearCollectionProgress(collectionId)
        }
    }
}
