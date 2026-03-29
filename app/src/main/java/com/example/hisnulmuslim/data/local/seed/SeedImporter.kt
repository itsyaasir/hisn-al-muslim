package com.example.hisnulmuslim.data.local.seed

import androidx.room.withTransaction
import com.example.hisnulmuslim.core.util.TimeProvider
import com.example.hisnulmuslim.data.local.db.HisnulMuslimDatabase
import com.example.hisnulmuslim.data.local.entity.SeedStatusEntity
import com.example.hisnulmuslim.data.mapper.toEntity
import javax.inject.Inject

class SeedImporter @Inject constructor(
    private val database: HisnulMuslimDatabase,
    private val seedDataSource: SeedDataSource,
    private val timeProvider: TimeProvider,
) {
    suspend fun importIfNeeded() {
        val dhikrCount = database.dhikrDao().count()
        if (dhikrCount > 0) {
            return
        }

        val dataset = seedDataSource.loadDataset()
        database.withTransaction {
            val currentDhikr = database.dhikrDao().count()
            if (currentDhikr == 0) {
                database.dhikrDao().insertAll(
                    dataset.adhkar.mapIndexed { index, item -> item.toEntity(orderIndex = index + 1) },
                )
                database.seedStatusDao().upsert(
                    SeedStatusEntity(
                        version = dataset.seedVersion,
                        importedAt = timeProvider.now(),
                    ),
                )
            } else if (database.seedStatusDao().getStatus() == null) {
                database.seedStatusDao().upsert(
                    SeedStatusEntity(
                        version = dataset.seedVersion,
                        importedAt = timeProvider.now(),
                    ),
                )
            }
        }
    }
}
