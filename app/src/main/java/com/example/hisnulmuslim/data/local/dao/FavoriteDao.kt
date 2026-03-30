package com.example.hisnulmuslim.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hisnulmuslim.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE dhikrId = :dhikrId)")
    fun observeIsFavorite(dhikrId: Long): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE dhikrId = :dhikrId)")
    suspend fun isFavorite(dhikrId: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE dhikrId = :dhikrId")
    suspend fun deleteByDhikrId(dhikrId: Long)

    @Query("DELETE FROM favorites")
    suspend fun deleteAll()
}
