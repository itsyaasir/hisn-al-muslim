package com.yasir.hisnalmuslim.core.model

import androidx.compose.runtime.Immutable

@Immutable
data class Dhikr(
    val id: Long,
    val collectionId: Long,
    val collectionTitle: String,
    val collectionSubtitle: String?,
    val collectionOrderIndex: Int,
    val title: String,
    val arabicText: String,
    val transliteration: String?,
    val translation: String?,
    val repeatCount: Int?,
    val notes: String?,
    val sourceReference: String?,
    val orderIndex: Int,
    val tags: List<String>,
)
