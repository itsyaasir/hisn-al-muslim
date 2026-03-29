package com.example.hisnulmuslim.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hisnulmuslim.data.local.entity.DhikrEntity
import com.example.hisnulmuslim.data.local.entity.DhikrRow
import kotlinx.coroutines.flow.Flow

@Dao
interface DhikrDao {
    @Query(
        """
        SELECT
            adhkar.id,
            adhkar.title,
            adhkar.arabicText,
            adhkar.transliteration,
            adhkar.translation,
            adhkar.repeatCount,
            adhkar.notes,
            adhkar.sourceReference,
            adhkar.orderIndex,
            adhkar.tags
        FROM adhkar
        WHERE adhkar.id = :dhikrId
        """,
    )
    fun observeDetail(dhikrId: Long): Flow<DhikrRow?>

    @Query(
        """
        SELECT
            adhkar.id,
            adhkar.title,
            adhkar.arabicText,
            adhkar.transliteration,
            adhkar.translation,
            adhkar.repeatCount,
            adhkar.notes,
            adhkar.sourceReference,
            adhkar.orderIndex,
            adhkar.tags
        FROM adhkar
        WHERE
            adhkar.title LIKE '%' || :query || '%' COLLATE NOCASE OR
            adhkar.arabicText LIKE '%' || :query || '%' OR
            IFNULL(adhkar.transliteration, '') LIKE '%' || :query || '%' COLLATE NOCASE OR
            IFNULL(adhkar.translation, '') LIKE '%' || :query || '%' COLLATE NOCASE OR
            IFNULL(adhkar.notes, '') LIKE '%' || :query || '%' COLLATE NOCASE OR
            IFNULL(adhkar.sourceReference, '') LIKE '%' || :query || '%' COLLATE NOCASE OR
            IFNULL(adhkar.tags, '') LIKE '%' || :query || '%' COLLATE NOCASE
        ORDER BY adhkar.orderIndex, adhkar.title
        """,
    )
    fun search(query: String): Flow<List<DhikrRow>>

    @Query(
        """
        SELECT
            adhkar.id,
            adhkar.title,
            adhkar.arabicText,
            adhkar.transliteration,
            adhkar.translation,
            adhkar.repeatCount,
            adhkar.notes,
            adhkar.sourceReference,
            adhkar.orderIndex,
            adhkar.tags
        FROM adhkar
        INNER JOIN favorites ON favorites.dhikrId = adhkar.id
        ORDER BY favorites.createdAt DESC
        """,
    )
    fun observeFavorites(): Flow<List<DhikrRow>>

    @Query(
        """
        SELECT
            adhkar.id,
            adhkar.title,
            adhkar.arabicText,
            adhkar.transliteration,
            adhkar.translation,
            adhkar.repeatCount,
            adhkar.notes,
            adhkar.sourceReference,
            adhkar.orderIndex,
            adhkar.tags
        FROM adhkar
        ORDER BY adhkar.orderIndex, adhkar.title
        """,
    )
    fun observeAllOrdered(): Flow<List<DhikrRow>>

    @Query("SELECT COUNT(*) FROM adhkar")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(adhkar: List<DhikrEntity>)
}
