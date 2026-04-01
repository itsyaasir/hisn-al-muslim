package com.yasir.hisnalmuslim.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.Immutable
import com.yasir.hisnalmuslim.core.model.AppSettings
import com.yasir.hisnalmuslim.core.model.Collection
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
import kotlinx.coroutines.flow.stateIn

@Immutable
data class SearchUiState(
    val query: String = "",
    val results: List<Collection> = emptyList(),
    val settings: AppSettings = AppSettings(),
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    repository: DhikrRepository,
    settingsRepository: SettingsRepository,
) : ViewModel() {
    private val query = MutableStateFlow("")

    private val results = query
        .flatMapLatest(repository::searchCollections)

    val uiState: StateFlow<SearchUiState> = combine(
        query,
        results,
        settingsRepository.observeSettings(),
    ) { currentQuery, items, settings ->
        SearchUiState(
            query = currentQuery,
            results = items,
            settings = settings,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchUiState(),
    )

    fun onQueryChange(newValue: String) {
        query.value = newValue
    }
}
