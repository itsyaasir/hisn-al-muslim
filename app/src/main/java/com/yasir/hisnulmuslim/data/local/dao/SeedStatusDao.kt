package com.yasir.hisnulmuslim.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yasir.hisnulmuslim.data.local.entity.SeedStatusEntity

@Dao
interface SeedStatusDao {
    @Query("SELECT * FROM seed_status WHERE id = 1")
    suspend fun getStatus(): SeedStatusEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(status: SeedStatusEntity)
}
