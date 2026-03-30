package com.yasir.hisnalmuslim.data.local.entity

data class CollectionRow(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val orderIndex: Int,
    val firstDhikrId: Long,
    val itemCount: Int,
)
