package com.example.hisnulmuslim.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "adhkar")
data class DhikrEntity(
    @PrimaryKey val id: Long,
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
