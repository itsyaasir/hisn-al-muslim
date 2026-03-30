package com.yasir.hisnalmuslim.data.local.seed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeedDataset(
    val seedVersion: Int,
    @SerialName("categories")
    val collections: List<SeedCollection> = emptyList(),
    val adhkar: List<SeedDhikr>,
)

@Serializable
data class SeedCollection(
    val id: Long,
    val title: String,
    val subtitle: String? = null,
    val orderIndex: Int,
)

@Serializable
data class SeedDhikr(
    val id: Long,
    @SerialName("categoryId")
    val collectionId: Long,
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
