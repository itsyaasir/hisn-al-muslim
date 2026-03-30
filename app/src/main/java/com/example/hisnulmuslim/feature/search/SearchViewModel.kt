package com.example.hisnulmuslim.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.Immutable
import com.example.hisnulmuslim.core.model.Dhikr
import com.example.hisnulmuslim.data.repository.DhikrRepository
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
    val results: List<Dhikr> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    repository: DhikrRepository,
) : ViewModel() {
    private val query = MutableStateFlow("")

    private val results = query
        .flatMapLatest(repository::searchDhikr)

    val uiState: StateFlow<SearchUiState> = combine(query, results) { currentQuery, items ->
        SearchUiState(
            query = currentQuery,
            results = items,
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
