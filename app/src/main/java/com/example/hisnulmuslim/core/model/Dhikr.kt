package com.example.hisnulmuslim.core.model

data class Dhikr(
    val id: Long,
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
