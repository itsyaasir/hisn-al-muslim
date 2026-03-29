package com.example.hisnulmuslim.data.local.seed

import kotlinx.serialization.Serializable

@Serializable
data class SeedDataset(
    val seedVersion: Int,
    val adhkar: List<SeedDhikr>,
)

@Serializable
data class SeedDhikr(
    val id: Long,
    val title: String,
    val arabicText: String,
    val transliteration: String? = null,
    val translation: String? = null,
    val repeatCount: Int? = null,
    val notes: String? = null,
    val sourceReference: String? = null,
    val orderIndex: Int,
    val tags: List<String> = emptyList(),
)
