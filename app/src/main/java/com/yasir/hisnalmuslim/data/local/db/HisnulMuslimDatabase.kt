package com.yasir.hisnalmuslim.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yasir.hisnalmuslim.data.local.dao.DhikrDao
import com.yasir.hisnalmuslim.data.local.dao.FavoriteDao
import com.yasir.hisnalmuslim.data.local.dao.ProgressDao
import com.yasir.hisnalmuslim.data.local.dao.SeedStatusDao
import com.yasir.hisnalmuslim.data.local.entity.DhikrEntity
import com.yasir.hisnalmuslim.data.local.entity.FavoriteEntity
import com.yasir.hisnalmuslim.data.local.entity.ProgressEntity
import com.yasir.hisnalmuslim.data.local.entity.SeedStatusEntity

@Database(
    entities = [
        DhikrEntity::class,
        FavoriteEntity::class,
        ProgressEntity::class,
        SeedStatusEntity::class,
    ],
    version = 4,
    exportSchema = true,
)
@TypeConverters(RoomConverters::class)
abstract class HisnulMuslimDatabase : RoomDatabase() {
    abstract fun dhikrDao(): DhikrDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun progressDao(): ProgressDao
    abstract fun seedStatusDao(): SeedStatusDao
}
