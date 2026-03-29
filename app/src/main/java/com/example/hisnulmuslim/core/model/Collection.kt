package com.example.hisnulmuslim.core.model

data class Collection(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val orderIndex: Int,
    val firstDhikrId: Long,
    val itemCount: Int,
)
