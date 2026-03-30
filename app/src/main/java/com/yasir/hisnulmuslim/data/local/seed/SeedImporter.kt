package com.yasir.hisnulmuslim.data.local.seed

import androidx.room.withTransaction
import com.yasir.hisnulmuslim.core.util.TimeProvider
import com.yasir.hisnulmuslim.data.local.db.HisnulMuslimDatabase
import com.yasir.hisnulmuslim.data.local.entity.SeedStatusEntity
import com.yasir.hisnulmuslim.data.mapper.toEntity
import javax.inject.Inject

class SeedImporter @Inject constructor(
    private val database: HisnulMuslimDatabase,
    private val seedDataSource: AssetSeedDataSource,
    private val timeProvider: TimeProvider,
) {
    suspend fun importIfNeeded() {
        val dhikrCount = database.dhikrDao().count()
        if (dhikrCount > 0) {
            return
        }

        val dataset = seedDataSource.loadDataset()
        val collectionsById = dataset.collections.associateBy { it.id }
        database.withTransaction {
            val currentDhikr = database.dhikrDao().count()
            if (currentDhikr == 0) {
                database.dhikrDao().insertAll(
                    dataset.adhkar.map { item ->
                        item.toEntity(
                            collection = checkNotNull(collectionsById[item.collectionId]) {
                                "Missing collection ${item.collectionId} for dhikr ${item.id}"
                            },
                            orderIndex = item.orderIndex,
                        )
                    },
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
