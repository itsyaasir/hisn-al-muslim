package com.yasir.hisnalmuslim.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.Immutable
import com.yasir.hisnalmuslim.core.model.Collection
import com.yasir.hisnalmuslim.core.model.Dhikr
import com.yasir.hisnalmuslim.core.util.TimeProvider
import com.yasir.hisnalmuslim.data.repository.DhikrRepository
import com.yasir.hisnalmuslim.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@Immutable
data class HomeUiState(
    val collections: List<Collection> = emptyList(),
    val dailyHighlight: Dhikr? = null,
    val canAdvanceDailyHighlight: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DhikrRepository,
    private val settingsRepository: SettingsRepository,
    private val timeProvider: TimeProvider,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repository.observeCollections(),
        repository.observeAllDhikrOrdered(),
        settingsRepository.observeDailyReflectionOffset(),
    ) { collections, allDhikr, dailyReflectionOffset ->
        HomeUiState(
            collections = collections,
            dailyHighlight = resolveDailyHighlight(allDhikr, dailyReflectionOffset),
            canAdvanceDailyHighlight = allDhikr.size > 1,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
    )

    fun showAnotherReflection() {
        viewModelScope.launch {
            settingsRepository.advanceDailyReflection()
        }
    }

    private fun resolveDailyHighlight(
        items: List<Dhikr>,
        offset: Long,
    ): Dhikr? {
        if (items.isEmpty()) {
            return null
        }
        val epochDay = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(timeProvider.now())
        val index = Math.floorMod(epochDay + offset, items.size.toLong()).toInt()
        return items[index]
    }
}
