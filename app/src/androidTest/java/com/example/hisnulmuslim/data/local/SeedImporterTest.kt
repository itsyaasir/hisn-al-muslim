package com.example.hisnulmuslim.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hisnulmuslim.core.util.TimeProvider
import com.example.hisnulmuslim.data.local.db.HisnulMuslimDatabase
import com.example.hisnulmuslim.data.local.seed.SeedCategory
import com.example.hisnulmuslim.data.local.seed.SeedDataSource
import com.example.hisnulmuslim.data.local.seed.SeedDataset
import com.example.hisnulmuslim.data.local.seed.SeedDhikr
import com.example.hisnulmuslim.data.local.seed.SeedImporter
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SeedImporterTest {

    private lateinit var database: HisnulMuslimDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, HisnulMuslimDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun importIfNeededSeedsOnlyOnce() = runTest {
        val importer = SeedImporter(
            database = database,
            seedDataSource = object : SeedDataSource {
                override suspend fun loadDataset(): SeedDataset {
                    return SeedDataset(
                        seedVersion = 7,
                        categories = listOf(
                            SeedCategory(1, "Morning", null, 1),
                        ),
                        adhkar = listOf(
                            SeedDhikr(
                                id = 1,
                                categoryId = 1,
                                title = "Morning",
                                arabicText = "اللهم بك أصبحنا",
                                translation = "Morning supplication",
                                orderIndex = 1,
                            ),
                        ),
                    )
                }
            },
            timeProvider = TimeProvider { 777L },
        )

        importer.importIfNeeded()
        importer.importIfNeeded()

        assertEquals(1, database.categoryDao().count())
        assertEquals(1, database.dhikrDao().count())
        assertEquals(7, database.seedStatusDao().getStatus()?.version)
    }
}
