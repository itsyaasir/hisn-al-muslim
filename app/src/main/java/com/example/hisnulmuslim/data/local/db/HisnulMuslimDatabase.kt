package com.example.hisnulmuslim.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.hisnulmuslim.data.local.dao.DhikrDao
import com.example.hisnulmuslim.data.local.dao.FavoriteDao
import com.example.hisnulmuslim.data.local.dao.ProgressDao
import com.example.hisnulmuslim.data.local.dao.SeedStatusDao
import com.example.hisnulmuslim.data.local.entity.DhikrEntity
import com.example.hisnulmuslim.data.local.entity.FavoriteEntity
import com.example.hisnulmuslim.data.local.entity.ProgressEntity
import com.example.hisnulmuslim.data.local.entity.SeedStatusEntity

@Database(
    entities = [
        DhikrEntity::class,
        FavoriteEntity::class,
        ProgressEntity::class,
        SeedStatusEntity::class,
    ],
    version = 3,
    exportSchema = true,
)
@TypeConverters(RoomConverters::class)
abstract class HisnulMuslimDatabase : RoomDatabase() {
    abstract fun dhikrDao(): DhikrDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun progressDao(): ProgressDao
    abstract fun seedStatusDao(): SeedStatusDao
}
