package com.yasir.hisnalmuslim.feature.adhkardetail

import androidx.lifecycle.SavedStateHandle
import com.yasir.hisnalmuslim.MainDispatcherRule
import com.yasir.hisnalmuslim.core.model.AppSettings
import com.yasir.hisnalmuslim.core.model.Category
import com.yasir.hisnalmuslim.core.model.Dhikr
import com.yasir.hisnalmuslim.core.model.DhikrProgress
import com.yasir.hisnalmuslim.core.model.ThemeMode
import com.yasir.hisnalmuslim.data.repository.DhikrRepository
import com.yasir.hisnalmuslim.data.repository.SettingsRepository
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class DhikrDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun incrementCounterCompletesRoundAndResetsCurrentCount() = runTest {
        val repository = FakeDetailRepository()
        val settingsRepository = FakeSettingsRepository()
        val viewModel = DhikrDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("dhikrId" to 1L, "categoryId" to 1L)),
            repository = repository,
            settingsRepository = settingsRepository,
        )

        advanceUntilIdle()
        viewModel.incrementCounter()
        viewModel.incrementCounter()
        viewModel.incrementCounter()
        advanceUntilIdle()

        assertEquals(0, repository.progress.value?.currentCount)
        assertEquals(1, repository.progress.value?.completedCount)
    }

    @Test
    fun toggleTranslationUpdatesSettingsRepository() = runTest {
        val repository = FakeDetailRepository()
        val settingsRepository = FakeSettingsRepository()
        val viewModel = DhikrDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("dhikrId" to 1L, "categoryId" to 1L)),
            repository = repository,
            settingsRepository = settingsRepository,
        )

        advanceUntilIdle()
        viewModel.setShowTranslation(false)
        advanceUntilIdle()

        assertEquals(false, settingsRepository.settings.value.showTranslation)
    }
}

private class FakeDetailRepository : DhikrRepository {
    val detail = MutableStateFlow(
        Dhikr(
            id = 1,
            categoryId = 1,
            categoryTitle = "Morning",
            title = "Morning remembrance",
            arabicText = "اللهم بك أصبحنا",
            transliteration = "Allahumma bika asbahna",
            translation = "Morning supplication",
            repeatCount = 3,
            notes = null,
            sourceReference = "Tirmidhi",
            orderIndex = 1,
            tags = listOf("morning"),
        ),
    )
    val progress = MutableStateFlow<DhikrProgress?>(DhikrProgress(1, 0, 0, 0))

    override fun observeCategories(): Flow<List<Category>> = flowOf(emptyList())
    override fun observeCategory(categoryId: Long): Flow<Category?> = flowOf(null)
    override fun observeDhikrByCategory(categoryId: Long): Flow<List<Dhikr>> = flowOf(listOf(detail.value))
    override fun observeDhikrDetail(dhikrId: Long): Flow<Dhikr?> = detail
    override fun observeSiblingDhikr(categoryId: Long): Flow<List<Dhikr>> = flowOf(listOf(detail.value))
    override fun observeFavorites(): Flow<List<Dhikr>> = flowOf(emptyList())
    override fun observeRecent(limit: Int): Flow<List<Dhikr>> = flowOf(emptyList())
    override fun observeDailyHighlight(): Flow<Dhikr?> = flowOf(null)
    override fun searchDhikr(query: String): Flow<List<Dhikr>> = flowOf(emptyList())
    override fun observeIsFavorite(dhikrId: Long): Flow<Boolean> = flowOf(false)
    override fun observeProgress(dhikrId: Long): Flow<DhikrProgress?> = progress
    override suspend fun toggleFavorite(dhikrId: Long) = Unit
    override suspend fun markOpened(dhikrId: Long) = Unit
    override suspend fun updateProgress(dhikrId: Long, currentCount: Int, completedCount: Int) {
        progress.value = DhikrProgress(dhikrId, currentCount, completedCount, 0)
    }
    override suspend fun resetProgress() = Unit
}

private class FakeSettingsRepository : SettingsRepository {
    val settings = MutableStateFlow(AppSettings(themeMode = ThemeMode.SYSTEM))

    override fun observeSettings(): Flow<AppSettings> = settings
    override suspend fun setThemeMode(mode: ThemeMode) {
        settings.value = settings.value.copy(themeMode = mode)
    }
    override suspend fun setDynamicColor(enabled: Boolean) {
        settings.value = settings.value.copy(dynamicColorEnabled = enabled)
    }
    override suspend fun setFontScale(scale: Float) {
        settings.value = settings.value.copy(fontScale = scale)
    }
    override suspend fun setArabicFontScale(scale: Float) {
        settings.value = settings.value.copy(arabicFontScale = scale)
    }
    override suspend fun setShowTransliteration(visible: Boolean) {
        settings.value = settings.value.copy(showTransliteration = visible)
    }
    override suspend fun setShowTranslation(visible: Boolean) {
        settings.value = settings.value.copy(showTranslation = visible)
    }
    override suspend fun setOnboardingCompleted(completed: Boolean) {
        settings.value = settings.value.copy(onboardingCompleted = completed)
    }
}
