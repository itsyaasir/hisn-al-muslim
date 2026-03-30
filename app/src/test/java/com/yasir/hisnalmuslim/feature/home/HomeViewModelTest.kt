package com.yasir.hisnalmuslim.feature.home

import com.yasir.hisnalmuslim.MainDispatcherRule
import com.yasir.hisnalmuslim.core.model.AppSettings
import com.yasir.hisnalmuslim.core.model.Category
import com.yasir.hisnalmuslim.core.model.Dhikr
import com.yasir.hisnalmuslim.core.model.DhikrProgress
import com.yasir.hisnalmuslim.data.repository.DhikrRepository
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun uiStateCombinesCategoriesRecentFavoritesAndHighlight() = runTest {
        val repository = FakeDhikrRepository().apply {
            categories.value = listOf(Category(1, "Morning", null, 1))
            recent.value = listOf(sampleDhikr())
            favorites.value = listOf(sampleDhikr())
            dailyHighlight.value = sampleDhikr(id = 2)
        }

        val viewModel = HomeViewModel(repository)
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.categories.size)
        assertEquals(1, viewModel.uiState.value.favorites.size)
        assertEquals(2L, viewModel.uiState.value.dailyHighlight?.id)
    }
}

private class FakeDhikrRepository : DhikrRepository {
    val categories = MutableStateFlow<List<Category>>(emptyList())
    val recent = MutableStateFlow<List<Dhikr>>(emptyList())
    val favorites = MutableStateFlow<List<Dhikr>>(emptyList())
    val dailyHighlight = MutableStateFlow<Dhikr?>(null)

    override fun observeCategories(): Flow<List<Category>> = categories
    override fun observeCategory(categoryId: Long): Flow<Category?> = flowOf(categories.value.firstOrNull())
    override fun observeDhikrByCategory(categoryId: Long): Flow<List<Dhikr>> = flowOf(emptyList())
    override fun observeDhikrDetail(dhikrId: Long): Flow<Dhikr?> = flowOf(null)
    override fun observeSiblingDhikr(categoryId: Long): Flow<List<Dhikr>> = flowOf(emptyList())
    override fun observeFavorites(): Flow<List<Dhikr>> = favorites
    override fun observeRecent(limit: Int): Flow<List<Dhikr>> = recent
    override fun observeDailyHighlight(): Flow<Dhikr?> = dailyHighlight
    override fun searchDhikr(query: String): Flow<List<Dhikr>> = flowOf(emptyList())
    override fun observeIsFavorite(dhikrId: Long): Flow<Boolean> = flowOf(false)
    override fun observeProgress(dhikrId: Long): Flow<DhikrProgress?> = flowOf(null)
    override suspend fun toggleFavorite(dhikrId: Long) = Unit
    override suspend fun markOpened(dhikrId: Long) = Unit
    override suspend fun updateProgress(dhikrId: Long, currentCount: Int, completedCount: Int) = Unit
    override suspend fun resetProgress() = Unit
}

private fun sampleDhikr(id: Long = 1) = Dhikr(
    id = id,
    categoryId = 1,
    categoryTitle = "Morning",
    title = "Morning remembrance",
    arabicText = "اللهم بك أصبحنا",
    transliteration = null,
    translation = "Morning supplication",
    repeatCount = 1,
    notes = null,
    sourceReference = "Tirmidhi",
    orderIndex = 1,
    tags = listOf("morning"),
)
