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
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class DhikrRepository @Inject constructor(
    private val dhikrDao: DhikrDao,
    private val favoriteDao: FavoriteDao,
    private val progressDao: ProgressDao,
    private val timeProvider: TimeProvider,
) {

    fun observeCollections(): Flow<List<Collection>> {
        return dhikrDao.observeCollections().map { items -> items.map { it.toModel() } }
    }

    fun observeCollectionDhikr(collectionId: Long): Flow<List<Dhikr>> {
        return dhikrDao.observeCollectionDhikr(collectionId).map { items -> items.map { it.toModel() } }
    }

    fun observeAllDhikrOrdered(): Flow<List<Dhikr>> {
        return dhikrDao.observeAllOrdered().map { items -> items.map { it.toModel() } }
    }

    fun observeFavorites(): Flow<List<Dhikr>> {
        return dhikrDao.observeFavorites().map { items -> items.map { it.toModel() } }
    }

    fun observeDailyHighlight(): Flow<Dhikr?> {
        return dhikrDao.observeAllOrdered().map { items ->
            if (items.isEmpty()) {
                null
            } else {
                val epochDay = TimeUnit.MILLISECONDS.toDays(timeProvider.now())
                val dayIndex = (epochDay % items.size).toInt()
                items[dayIndex].toModel()
            }
        }
    }

    fun searchDhikr(query: String): Flow<List<Dhikr>> {
        val normalizedQuery = SearchQueryNormalizer.normalize(query)
        if (normalizedQuery.isBlank()) {
            return flowOf(emptyList())
        }
        return dhikrDao.search(normalizedQuery).map { items -> items.map { it.toModel() } }
    }

    fun observeIsFavorite(dhikrId: Long): Flow<Boolean> {
        return favoriteDao.observeIsFavorite(dhikrId)
    }

    fun observeProgress(dhikrId: Long): Flow<DhikrProgress?> {
        return progressDao.observeByDhikrId(dhikrId).map { it?.toModel() }
    }

    suspend fun toggleFavorite(dhikrId: Long) {
        val isFavorite = favoriteDao.isFavorite(dhikrId)
        if (isFavorite) {
            removeFavorite(dhikrId)
        } else {
            addFavorite(dhikrId)
        }
    }

    suspend fun addFavorite(dhikrId: Long) {
        favoriteDao.upsert(
            FavoriteEntity(
                dhikrId = dhikrId,
                createdAt = timeProvider.now(),
            ),
        )
    }

    suspend fun removeFavorite(dhikrId: Long) {
        favoriteDao.deleteByDhikrId(dhikrId)
    }

    suspend fun updateProgress(dhikrId: Long, currentCount: Int, completedCount: Int) {
        progressDao.upsert(
            ProgressEntity(
                dhikrId = dhikrId,
                currentCount = currentCount,
                completedCount = completedCount,
                updatedAt = timeProvider.now(),
            ),
        )
    }

    suspend fun clearCollectionProgress(collectionId: Long) {
        progressDao.deleteByCollectionId(collectionId)
    }

    suspend fun clearFavorites() {
        favoriteDao.deleteAll()
    }
}
