package com.example.hisnulmuslim.data.local.entity

data class DhikrRow(
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
