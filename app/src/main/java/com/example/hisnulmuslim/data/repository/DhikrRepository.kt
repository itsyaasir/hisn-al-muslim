package com.example.hisnulmuslim.data.repository

import com.example.hisnulmuslim.core.model.Collection
import com.example.hisnulmuslim.core.model.Dhikr
import com.example.hisnulmuslim.core.model.DhikrProgress
import kotlinx.coroutines.flow.Flow

interface DhikrRepository {
    fun observeCollections(): Flow<List<Collection>>
    fun observeCollectionDhikr(collectionId: Long): Flow<List<Dhikr>>
    fun observeAllDhikrOrdered(): Flow<List<Dhikr>>
    fun observeFavorites(): Flow<List<Dhikr>>
    fun observeDailyHighlight(): Flow<Dhikr?>
    fun searchDhikr(query: String): Flow<List<Dhikr>>
    fun observeIsFavorite(dhikrId: Long): Flow<Boolean>
    fun observeProgress(dhikrId: Long): Flow<DhikrProgress?>
    suspend fun toggleFavorite(dhikrId: Long)
    suspend fun updateProgress(dhikrId: Long, currentCount: Int, completedCount: Int)
    suspend fun clearCollectionProgress(collectionId: Long)
    suspend fun clearFavorites()
}
