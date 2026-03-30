package com.yasir.hisnalmuslim.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yasir.hisnalmuslim.data.local.db.HisnulMuslimDatabase
import com.yasir.hisnalmuslim.data.local.entity.CategoryEntity
import com.yasir.hisnalmuslim.data.local.entity.DhikrEntity
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DhikrDaoTest {

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
    fun searchMatchesArabicAndTranslation() = runTest {
        database.categoryDao().insertAll(
            listOf(
                CategoryEntity(
                    id = 1,
                    title = "Morning",
                    subtitle = null,
                    orderIndex = 1,
                ),
            ),
        )
        database.dhikrDao().insertAll(
            listOf(
                DhikrEntity(
                    id = 10,
                    categoryId = 1,
                    title = "Protection",
                    arabicText = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ",
                    transliteration = "A'udhu bikalimatillah",
                    translation = "I seek refuge in the perfect words of Allah.",
                    repeatCount = 3,
                    notes = null,
                    sourceReference = "Muslim",
                    orderIndex = 1,
                    tags = listOf("protection"),
                ),
            ),
        )

        val byArabic = database.dhikrDao().search("كَلِمَات").first()
        val byTranslation = database.dhikrDao().search("perfect words").first()

        assertEquals(1, byArabic.size)
        assertEquals(1, byTranslation.size)
        assertEquals(10L, byArabic.first().id)
        assertTrue(byTranslation.first().translation!!.contains("perfect words"))
    }
}
