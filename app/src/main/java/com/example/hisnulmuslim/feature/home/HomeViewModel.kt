package com.example.hisnulmuslim.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.Immutable
import com.example.hisnulmuslim.core.model.Collection
import com.example.hisnulmuslim.core.model.Dhikr
import com.example.hisnulmuslim.data.repository.DhikrRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@Immutable
data class HomeUiState(
    val collections: List<Collection> = emptyList(),
    val dailyHighlight: Dhikr? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: DhikrRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repository.observeCollections(),
        repository.observeDailyHighlight(),
    ) { collections, dailyHighlight ->
        HomeUiState(
            collections = collections,
            dailyHighlight = dailyHighlight,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
    )
}
