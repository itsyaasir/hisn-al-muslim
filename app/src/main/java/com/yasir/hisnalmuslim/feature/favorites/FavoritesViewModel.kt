package com.yasir.hisnalmuslim.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yasir.hisnalmuslim.core.model.Dhikr
import com.yasir.hisnalmuslim.data.repository.DhikrRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: DhikrRepository,
) : ViewModel() {
    val uiState: StateFlow<List<Dhikr>> = repository.observeFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
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
