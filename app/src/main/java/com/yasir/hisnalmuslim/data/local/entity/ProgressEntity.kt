package com.yasir.hisnalmuslim.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "progress",
    primaryKeys = ["dhikrId"],
    foreignKeys = [
        ForeignKey(
            entity = DhikrEntity::class,
            parentColumns = ["id"],
            childColumns = ["dhikrId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("dhikrId")],
)
data class ProgressEntity(
    val dhikrId: Long,
    val currentCount: Int,
    val completedCount: Int,
    val updatedAt: Long,
)
