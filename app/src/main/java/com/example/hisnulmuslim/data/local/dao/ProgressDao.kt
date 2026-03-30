package com.example.hisnulmuslim.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hisnulmuslim.data.local.entity.ProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Query("SELECT * FROM progress WHERE dhikrId = :dhikrId")
    fun observeByDhikrId(dhikrId: Long): Flow<ProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ProgressEntity)

    @Query(
        """
        DELETE FROM progress
        WHERE dhikrId IN (
            SELECT id
            FROM adhkar
            WHERE collectionId = :collectionId
        )
        """,
    )
    suspend fun deleteByCollectionId(collectionId: Long)

    @Query("DELETE FROM progress")
    suspend fun deleteAll()
}
