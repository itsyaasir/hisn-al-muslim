package com.example.hisnulmuslim.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hisnulmuslim.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM favorites
            INNER JOIN adhkar ON adhkar.id = favorites.dhikrId
            WHERE adhkar.collectionId = :collectionId
        )
        """,
    )
    fun observeIsCollectionFavorite(collectionId: Long): Flow<Boolean>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM favorites
            INNER JOIN adhkar ON adhkar.id = favorites.dhikrId
            WHERE adhkar.collectionId = :collectionId
        )
        """,
    )
    suspend fun isCollectionFavorite(collectionId: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<FavoriteEntity>)

    @Query("DELETE FROM favorites WHERE dhikrId IN (SELECT id FROM adhkar WHERE collectionId = :collectionId)")
    suspend fun deleteByCollectionId(collectionId: Long)

    @Query("DELETE FROM favorites")
    suspend fun deleteAll()
}
