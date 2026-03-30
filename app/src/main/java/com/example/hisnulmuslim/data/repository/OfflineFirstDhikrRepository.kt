package com.example.hisnulmuslim.data.repository

import com.example.hisnulmuslim.core.model.Collection
import com.example.hisnulmuslim.core.model.Dhikr
import com.example.hisnulmuslim.core.model.DhikrProgress
import com.example.hisnulmuslim.core.util.SearchQueryNormalizer
import com.example.hisnulmuslim.core.util.TimeProvider
import com.example.hisnulmuslim.data.local.dao.DhikrDao
import com.example.hisnulmuslim.data.local.dao.FavoriteDao
import com.example.hisnulmuslim.data.local.dao.ProgressDao
import com.example.hisnulmuslim.data.local.entity.FavoriteEntity
import com.example.hisnulmuslim.data.local.entity.ProgressEntity
import com.example.hisnulmuslim.data.mapper.toModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class OfflineFirstDhikrRepository @Inject constructor(
    private val dhikrDao: DhikrDao,
    private val favoriteDao: FavoriteDao,
    private val progressDao: ProgressDao,
    private val timeProvider: TimeProvider,
) : DhikrRepository {

    override fun observeCollections(): Flow<List<Collection>> {
        return dhikrDao.observeCollections().map { items -> items.map { it.toModel() } }
    }

    override fun observeCollectionDhikr(collectionId: Long): Flow<List<Dhikr>> {
        return dhikrDao.observeCollectionDhikr(collectionId).map { items -> items.map { it.toModel() } }
    }

    override fun observeAllDhikrOrdered(): Flow<List<Dhikr>> {
        return dhikrDao.observeAllOrdered().map { items -> items.map { it.toModel() } }
    }

    override fun observeFavorites(): Flow<List<Dhikr>> {
        return dhikrDao.observeFavorites().map { items -> items.map { it.toModel() } }
    }

    override fun observeDailyHighlight(): Flow<Dhikr?> {
        return dhikrDao.observeAllOrdered().map { items ->
            if (items.isEmpty()) {
                null
            } else {
                val dayIndex = (LocalDate.now().toEpochDay() % items.size).toInt()
                items[dayIndex].toModel()
            }
        }
    }

    override fun searchDhikr(query: String): Flow<List<Dhikr>> {
        val normalizedQuery = SearchQueryNormalizer.normalize(query)
        if (normalizedQuery.isBlank()) {
            return flowOf(emptyList())
        }
        return dhikrDao.search(normalizedQuery).map { items -> items.map { it.toModel() } }
    }

    override fun observeIsFavorite(dhikrId: Long): Flow<Boolean> {
        return favoriteDao.observeIsFavorite(dhikrId)
    }

    override fun observeProgress(dhikrId: Long): Flow<DhikrProgress?> {
        return progressDao.observeByDhikrId(dhikrId).map { it?.toModel() }
    }

    override suspend fun toggleFavorite(dhikrId: Long) {
        if (favoriteDao.isFavorite(dhikrId)) {
            favoriteDao.deleteByDhikrId(dhikrId)
        } else {
            favoriteDao.upsert(
                FavoriteEntity(
                    dhikrId = dhikrId,
                    createdAt = timeProvider.now(),
                ),
            )
        }
    }

    override suspend fun updateProgress(dhikrId: Long, currentCount: Int, completedCount: Int) {
        progressDao.upsert(
            ProgressEntity(
                dhikrId = dhikrId,
                currentCount = currentCount,
                completedCount = completedCount,
                updatedAt = timeProvider.now(),
            ),
        )
    }

    override suspend fun clearCollectionProgress(collectionId: Long) {
        progressDao.deleteByCollectionId(collectionId)
    }

    override suspend fun clearFavorites() {
        favoriteDao.deleteAll()
    }
}
