package com.example.hisnulmuslim.core.model

import androidx.compose.runtime.Immutable

@Immutable
data class Collection(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val orderIndex: Int,
    val firstDhikrId: Long,
    val itemCount: Int,
)
