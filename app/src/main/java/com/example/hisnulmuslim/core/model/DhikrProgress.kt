package com.example.hisnulmuslim.core.model

import androidx.compose.runtime.Immutable

@Immutable
data class DhikrProgress(
    val dhikrId: Long,
    val currentCount: Int,
    val completedCount: Int,
    val updatedAt: Long,
)
