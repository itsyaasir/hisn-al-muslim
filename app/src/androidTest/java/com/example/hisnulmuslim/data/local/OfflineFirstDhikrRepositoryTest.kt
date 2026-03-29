package com.example.hisnulmuslim.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hisnulmuslim.core.util.TimeProvider
import com.example.hisnulmuslim.data.local.db.HisnulMuslimDatabase
import com.example.hisnulmuslim.data.local.entity.CategoryEntity
import com.example.hisnulmuslim.data.local.entity.DhikrEntity
import com.example.hisnulmuslim.data.repository.OfflineFirstDhikrRepository
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OfflineFirstDhikrRepositoryTest {

    private lateinit var database: HisnulMuslimDatabase
    private lateinit var repository: OfflineFirstDhikrRepository

    @Before
    fun setUp() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, HisnulMuslimDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = OfflineFirstDhikrRepository(
            categoryDao = database.categoryDao(),
            dhikrDao = database.dhikrDao(),
            favoriteDao = database.favoriteDao(),
            recentReadDao = database.recentReadDao(),
            progressDao = database.progressDao(),
            timeProvider = TimeProvider { 1_000L },
        )
        database.categoryDao().insertAll(
            listOf(
                CategoryEntity(1, "Morning", null, 1),
            ),
        )
        database.dhikrDao().insertAll(
            listOf(
                DhikrEntity(
                    id = 1,
                    categoryId = 1,
                    title = "Morning remembrance",
                    arabicText = "اللهم بك أصبحنا",
                    transliteration = null,
                    translation = "O Allah, by You we enter the morning.",
                    repeatCount = 1,
                    notes = null,
                    sourceReference = "Tirmidhi",
                    orderIndex = 1,
                    tags = listOf("morning"),
                ),
            ),
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun toggleFavoriteUpdatesFavoriteStream() = runTest {
        repository.toggleFavorite(1)

        val favorites = repository.observeFavorites().first()

        assertEquals(1, favorites.size)
        assertEquals(1L, favorites.first().id)
    }

    @Test
    fun markOpenedAndUpdateProgressPersistMetadata() = runTest {
        repository.markOpened(1)
        repository.updateProgress(dhikrId = 1, currentCount = 2, completedCount = 1)

        val recent = repository.observeRecent(limit = 1).first()
        val progress = repository.observeProgress(1).first()

        assertEquals(1, recent.size)
        assertEquals(2, progress?.currentCount)
        assertTrue(progress?.completedCount == 1)
    }
}
