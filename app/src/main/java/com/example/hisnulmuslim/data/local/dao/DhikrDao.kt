package com.example.hisnulmuslim.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hisnulmuslim.data.local.entity.CollectionRow
import com.example.hisnulmuslim.data.local.entity.DhikrEntity
import com.example.hisnulmuslim.data.local.entity.DhikrRow
import kotlinx.coroutines.flow.Flow

@Dao
interface DhikrDao {
    @Query(
        """
        SELECT
            adhkar.id,
            adhkar.collectionId,
            adhkar.collectionTitle,
            adhkar.collectionSubtitle,
            adhkar.collectionOrderIndex,
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
            adhkar.collectionId,
            adhkar.collectionTitle,
            adhkar.collectionSubtitle,
            adhkar.collectionOrderIndex,
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
        ORDER BY adhkar.collectionOrderIndex, adhkar.orderIndex, adhkar.title
        """,
    )
    fun observeAllOrdered(): Flow<List<DhikrRow>>

    @Query(
        """
        SELECT
            grouped.collectionId AS id,
            grouped.collectionTitle AS title,
            grouped.collectionSubtitle AS subtitle,
            grouped.collectionOrderIndex AS orderIndex,
            (
                SELECT innerAdhkar.id
                FROM adhkar AS innerAdhkar
                WHERE innerAdhkar.collectionId = grouped.collectionId
                ORDER BY innerAdhkar.orderIndex, innerAdhkar.id
                LIMIT 1
            ) AS firstDhikrId,
            COUNT(*) AS itemCount
        FROM adhkar AS grouped
        INNER JOIN favorites ON favorites.dhikrId = grouped.id
        GROUP BY
            grouped.collectionId,
            grouped.collectionTitle,
            grouped.collectionSubtitle,
            grouped.collectionOrderIndex
        ORDER BY MAX(favorites.createdAt) DESC, grouped.collectionOrderIndex, grouped.collectionTitle
        """,
    )
    fun observeFavoriteCollections(): Flow<List<CollectionRow>>

    @Query(
        """
        SELECT
            grouped.collectionId AS id,
            grouped.collectionTitle AS title,
            grouped.collectionSubtitle AS subtitle,
            grouped.collectionOrderIndex AS orderIndex,
            (
                SELECT innerAdhkar.id
                FROM adhkar AS innerAdhkar
                WHERE innerAdhkar.collectionId = grouped.collectionId
                ORDER BY innerAdhkar.orderIndex, innerAdhkar.id
                LIMIT 1
            ) AS firstDhikrId,
            COUNT(*) AS itemCount
        FROM adhkar AS grouped
        GROUP BY
            grouped.collectionId,
            grouped.collectionTitle,
            grouped.collectionSubtitle,
            grouped.collectionOrderIndex
        ORDER BY grouped.collectionOrderIndex, grouped.collectionTitle
        """,
    )
    fun observeCollections(): Flow<List<CollectionRow>>

    @Query(
        """
        SELECT
            adhkar.id,
            adhkar.collectionId,
            adhkar.collectionTitle,
            adhkar.collectionSubtitle,
            adhkar.collectionOrderIndex,
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
        WHERE adhkar.collectionId = :collectionId
        ORDER BY adhkar.orderIndex, adhkar.title
        """,
    )
    fun observeCollectionDhikr(collectionId: Long): Flow<List<DhikrRow>>

    @Query("SELECT COUNT(*) FROM adhkar")
    suspend fun count(): Int

    @Query("SELECT id FROM adhkar WHERE collectionId = :collectionId ORDER BY orderIndex, id")
    suspend fun getDhikrIdsForCollection(collectionId: Long): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(adhkar: List<DhikrEntity>)
}
