package com.example.hisnulmuslim.data.repository

import com.example.hisnulmuslim.core.model.Dhikr
import com.example.hisnulmuslim.core.model.DhikrProgress
import kotlinx.coroutines.flow.Flow

interface DhikrRepository {
    fun observeAllDhikrOrdered(): Flow<List<Dhikr>>
    fun observeDhikrDetail(dhikrId: Long): Flow<Dhikr?>
    fun observeFavorites(): Flow<List<Dhikr>>
    fun observeDailyHighlight(): Flow<Dhikr?>
    fun searchDhikr(query: String): Flow<List<Dhikr>>
    fun observeIsFavorite(dhikrId: Long): Flow<Boolean>
    fun observeProgress(dhikrId: Long): Flow<DhikrProgress?>
    suspend fun toggleFavorite(dhikrId: Long)
    suspend fun updateProgress(dhikrId: Long, currentCount: Int, completedCount: Int)
    suspend fun resetProgress()
}
